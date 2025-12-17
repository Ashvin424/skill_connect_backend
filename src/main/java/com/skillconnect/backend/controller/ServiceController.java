package com.skillconnect.backend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.skillconnect.backend.dtos.CreateServiceDTO;
import com.skillconnect.backend.dtos.ServiceResponseDTO;
import com.skillconnect.backend.dtos.UpdateServiceDTO;
import com.skillconnect.backend.models.User;
import com.skillconnect.backend.repository.RatingRepository;
import com.skillconnect.backend.repository.ServiceRepository;
import com.skillconnect.backend.repository.UserRepository;
import com.skillconnect.backend.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillconnect.backend.models.Service;
import com.skillconnect.backend.service.ServiceService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/services")
public class ServiceController {

    private final UserController userController;

    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private ServiceService serviceService;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;


    ServiceController(UserController userController) {
        this.userController = userController;
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponseDTO>> getAllServices(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   @RequestParam(required = false) String category) {
        List<Service> allServices = serviceService.getAllServices(page, size, category);
        List<ServiceResponseDTO> dtos = allServices.stream()
                .map(this::mapToDTO)
                .toList();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ServiceResponseDTO>> searchServices(
            @RequestParam String searchBy,
            @RequestParam String query
    ){
        List<Service> services = serviceService.searchServices(searchBy, query);
        List<ServiceResponseDTO> responseDtos = services.stream()
                .map(this::mapToDTO)
                .toList();
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ServiceResponseDTO> createService(@RequestBody CreateServiceDTO dto, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Service createdService = serviceService.createService(dto, user);
        ServiceResponseDTO responseDTO = mapToDTO(createdService);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }



    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponseDTO> getServiceById(@PathVariable Long id) {
        Optional<Service> service = serviceService.getServiceById(id);
        if (service.isPresent()) {
            ServiceResponseDTO responseDTO = mapToDTO(service.get());
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateServiceDTO> updateServiceById(@PathVariable Long id, @RequestBody UpdateServiceDTO dto, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Optional<Service> updateService = serviceService.updateService(id, dto, user);
        if (updateService.isPresent()){
            Service updatedService = updateService.get();
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteServiceById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Service service = serviceService.getServiceById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        if (!service.getPostedBy().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this service.");
        }
        serviceService.deleteServiceById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateService(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        serviceService.deactivateService(id, user.getId());
        return ResponseEntity.ok("Service deactivated successfully");
    }


    @PostMapping("/{id}/upload-images")
    public ResponseEntity<?> uploadServiceImages(
            @PathVariable("id") Long serviceId,
            @RequestParam("files") List<MultipartFile> files,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Service service = serviceService.getServiceById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        if (!service.getPostedBy().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to upload images for this service.");
        }

        try {
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                String url = cloudinaryService.uploadFile(file);
                imageUrls.add(url);
            }

            // Save URLs to your service entity if needed
            service.getImageUrls().addAll(imageUrls);
            serviceRepository.save(service);

            return ResponseEntity.ok(Map.of("imageUrls", imageUrls));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private ServiceResponseDTO mapToDTO(Service service) {
        ServiceResponseDTO dto = new ServiceResponseDTO();
        dto.setId(service.getId());
        dto.setTitle(service.getTitle());
        dto.setDescription(service.getDescription());
        dto.setCategory(service.getCategory());
        dto.setImageUrls(service.getImageUrls());
        dto.setProviderMode(service.getPostedBy().getServiceMode());
        dto.setIsActive(service.isActive());

        if (service.getPostedBy() != null) {
            dto.setUserId(service.getPostedBy().getId());
            dto.setUsername(service.getPostedBy().getName());
            dto.setUserProfileImageUrl(service.getPostedBy().getProfileImageUrl());
            Double avgRating = ratingRepository.findAverageRatingByUserId(service.getPostedBy().getId());
            dto.setUserRating(avgRating != null ? avgRating : 0.0);
        }

        return dto;
    }



//    @GetMapping("/{id}")
//    public ResponseEntity<ServiceDetailDTO> getServiceDetail(@PathVariable Long id) {
//        Service service = serviceRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Service not found"));
//
//        ServiceDetailDTO dto = new ServiceDetailDTO();
//        dto.setId(service.getId());
//        dto.setTitle(service.getTitle());
//        dto.setDescription(service.getDescription());
//        dto.setCategory(service.getCategory());
//        dto.setImageUrls(service.getImageUrls());
//        dto.setCreatedAt(service.getCreatedAt());
//        dto.setUserId(service.getPostedBy().getId());
//        dto.setUsername(service.getPostedBy().getName());
//
//        // Fetch rating
//        Double avgRating = ratingRepository.getAverageRatingByUserId(service.getPostedBy().getId());
//        Integer totalReviews = ratingRepository.countByUserId(service.getPostedBy().getId());
//
//        dto.setUserAverageRating(avgRating != null ? avgRating : 0.0);
//        dto.setTotalReviews(totalReviews != null ? totalReviews : 0);
//
//        return ResponseEntity.ok(dto);
//    }

}
