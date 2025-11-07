package com.skillconnect.backend.service;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.skillconnect.backend.customException.ResourceNotFoundException;
import com.skillconnect.backend.dtos.CreateServiceDTO;
import com.skillconnect.backend.dtos.UpdateServiceDTO;
import com.skillconnect.backend.models.User;
import com.skillconnect.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.skillconnect.backend.repository.ServiceRepository;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    // Method to create a new service
    // This method will save the service object to the database
    public com.skillconnect.backend.models.Service createService(CreateServiceDTO dto, User user)  {
        com.skillconnect.backend.models.Service service = new com.skillconnect.backend.models.Service();
        service.setPostedBy(user);
        service.setTitle(dto.getTitle());
        service.setDescription(dto.getDescription());
        service.setCategory(dto.getCategory());
        service.setImageUrls(dto.getImageUrls());

        return serviceRepository.save(service);
    }

    // Method to get all services
    // This method will retrieve all service objects from the database
    public List<com.skillconnect.backend.models.Service> getAllServices(int page, int size, String category) {
        Pageable pageable = PageRequest.of(page, size);
        if (category != null && !category.isBlank()) {
            return serviceRepository.findByCategoryIgnoreCase(category, pageable).getContent();
        }
        return serviceRepository.findAll();
    }

    // Method to get a service by its ID
    // This method will retrieve a service object by its ID from the database
    public Optional<com.skillconnect.backend.models.Service> getServiceById(Long id) {
        return serviceRepository.findById(id);
    }

    // Method to update a service by its ID
    // This method will update the service object in the database if it exists
    // If the service does not exist, it will return an empty Optional
    public Optional<com.skillconnect.backend.models.Service> updateService(Long id, UpdateServiceDTO dto, UserDetails userDetails) {
        Optional<com.skillconnect.backend.models.Service> existingServiceOpt = serviceRepository.findById(id);
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
        if (existingServiceOpt.isPresent()) {
            com.skillconnect.backend.models.Service existingService = getService(dto, existingServiceOpt, user.getId());

            return Optional.of(serviceRepository.save(existingService));
        }
        return Optional.empty();
    }

    private static com.skillconnect.backend.models.Service getService(UpdateServiceDTO dto, Optional<com.skillconnect.backend.models.Service> existingServiceOpt, Long id) {
        com.skillconnect.backend.models.Service existingService = existingServiceOpt.get();
        if (!existingService.getPostedBy().getId().equals(id)) {
            throw new SecurityException("Unauthorized to update this service.");
        }
        if (dto.getTitle() != null) existingService.setTitle(dto.getTitle());
        if (dto.getDescription() != null) existingService.setDescription(dto.getDescription());
        if (dto.getCategory() != null) existingService.setCategory(dto.getCategory());
        return existingService;
    }

    // Method to delete a service by its ID
    // This method will delete the service object from the database if it exists
    public boolean deleteServiceById(Long id) {
        if (serviceRepository.existsById(id)) {
            serviceRepository.deleteById(id);
            return true;
        }
        return false;       
    }

    // Method to upload service images
    // This method will save the uploaded images to the server and update the service's image URLs
    // This method returns a list of URLs of the uploaded images
//    # Service Image Upload (Multiple Images) :-->
//          - POST /services/{id}/upload-images
//          - Accepts: List<MultipartFile> named "images"
//          - Stores: List<String> imageUrls inside Service entity
//          - Saves files to: /uploads/services/
//          - Image access: localhost:8080/uploads/services/{filename}
//          - DB: Uses @ElementCollection to store list of URLs

    public List<String> uploadServiceImages(Long serviceId, List<MultipartFile> imageFiles) throws IOException {
        com.skillconnect.backend.models.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service Not Found"));
        Path uploadDir = Paths.get("uploads", "services");
        Files.createDirectories(uploadDir);

        List<String> imageUrls = new ArrayList<>();

        Path dirPath = Paths.get("uploads/services/");
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        for (MultipartFile file : imageFiles) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = dirPath.resolve(fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/uploads/services/" + fileName;
            imageUrls.add(imageUrl);
        }

        service.getImageUrls().addAll(imageUrls);
        serviceRepository.save(service);

        return imageUrls;
    }

    public List<com.skillconnect.backend.models.Service> searchServices(String searchBy, String query){
        switch (searchBy.toLowerCase()) {
            case "title":
                return serviceRepository.findByTitleContainingIgnoreCase(query);
            case "category":
                return serviceRepository.findByCategoryContainingIgnoreCase(query);
            case "username":
                return serviceRepository.findByPostedByUsernameContainingIgnoreCase(query);
            default:
                throw new IllegalArgumentException("Invalid search type: " + searchBy);
        }
    }


}
