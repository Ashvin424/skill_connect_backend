package com.skillconnect.backend.controller;

import com.skillconnect.backend.dtos.*;
import com.skillconnect.backend.models.Service;
import com.skillconnect.backend.models.User;
import com.skillconnect.backend.repository.RatingRepository;
import com.skillconnect.backend.repository.ServiceRepository;
import com.skillconnect.backend.repository.UserRepository;
import com.skillconnect.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @GetMapping("/{id}")  // get by id
    public ResponseEntity<ProfileResponseDTO> getUserById(@PathVariable Long id){
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()){
            User user = userOpt.get();
            String[] skillsArray = user.getSkills() != null ? user.getSkills().split(",") : new String[0];
            int skillCount = skillsArray.length;
            int  serviceCount = serviceRepository.countByPostedBy_Id(user.getId());
            int reviewCount = ratingRepository.countByReviewee_Id(user.getId());
            Double averageRatingDouble = ratingRepository.findAverageRatingByUserId(user.getId());
            double averageRating;
            if (averageRatingDouble != null) {
                averageRating = averageRatingDouble;
            } else {
                averageRating = 0.0; // Default value if no ratings exist
            }
            List<Service> services = serviceRepository.findAllByPostedBy_Id(user.getId());
            ProfileResponseDTO profile = new ProfileResponseDTO();
            profile.setDisplayUsername(user.getDisplayUsername());
            profile.setName(user.getName());
            profile.setEmail(user.getEmail());
            profile.setBio(user.getBio());
            profile.setLocation(user.getLocation());
            profile.setSkills(user.getSkills());
            profile.setProfileImageUrl(user.getProfileImageUrl());
            profile.setCreatedAt(user.getCreatedAt());
            profile.setSkillCount(skillCount);
            profile.setServiceCount(serviceCount);
            profile.setReviewCount(reviewCount);
            profile.setAverageRating(averageRating);
            profile.setServices(services);
            return new ResponseEntity<>(profile, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("profile/me")
    public ResponseEntity<ProfileResponseDTO> getUserProfile(@AuthenticationPrincipal UserDetails userDetails){
        User user = userService.getUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userDetails.getUsername()));

        String[] skillsArray = user.getSkills() != null ? user.getSkills().split(",") : new String[0];
        int skillCount = skillsArray.length;
        int  serviceCount = serviceRepository.countByPostedBy_Id(user.getId());
        int reviewCount = ratingRepository.countByReviewee_Id(user.getId());
        Double averageRatingDouble = ratingRepository.findAverageRatingByUserId(user.getId());
        double averageRating;
        if (averageRatingDouble != null) {
            averageRating = averageRatingDouble;
        } else {
            averageRating = 0.0; // Default value if no ratings exist
        }
        List<Service> services = serviceRepository.findAllByPostedBy_Id(user.getId());


        ProfileResponseDTO profile = new ProfileResponseDTO();
        profile.setDisplayUsername(user.getDisplayUsername());
        profile.setName(user.getName());
        profile.setEmail(user.getEmail());
        profile.setBio(user.getBio());
        profile.setLocation(user.getLocation());
        profile.setSkills(user.getSkills());
        profile.setProfileImageUrl(user.getProfileImageUrl());
        profile.setCreatedAt(user.getCreatedAt());
        profile.setSkillCount(skillCount);
        profile.setServiceCount(serviceCount);
        profile.setReviewCount(reviewCount);
        profile.setAverageRating(averageRating);
        profile.setServices(services);


        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @GetMapping("/username/{username}")  // get by username
    public ResponseEntity<User> getUserByUsername(@PathVariable String username){
        Optional<User> user = userService.getUserByUsername(username);
        if (user.isPresent()){
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}") // update user profile
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO dto) {
        User updatedUser = userService.updateUser(dto);
        return new ResponseEntity<>(updatedUser,HttpStatus.OK);
    }

    @PutMapping("/profile/update")
    public ResponseEntity<User> userProfileUpdate(@RequestBody UpdateProfileDTO profileDTO, @AuthenticationPrincipal UserDetails userDetails){
        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        try{
            User updatedUser = userService.updateProfile(email, profileDTO);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }
        catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

//    To upload a profile image:
//    We use @PostMapping("/upload-profile-image")
//    Accept @RequestParam MultipartFile file
//    Save the image file on disk (uploads/ folder)
//    Update User.profileImageUrl with image path

    @PostMapping("/profile/upload-image")
    public ResponseEntity<ProfileImageUploadResponseDTO> uploadProfileImage(
            @RequestParam("image") MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
            String email = userDetails.getUsername();
        try {
            String profileImageUrl = userService.uploadProfileImage(email, image);
            return ResponseEntity.ok(new ProfileImageUploadResponseDTO(profileImageUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("/change-password")
    public ResponseEntity<ChangePasswordDTO> changePassword(
            @RequestBody ChangePasswordDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        userService.changePassword(email, dto);
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setCurrentPassword(dto.getCurrentPassword());
        changePasswordDTO.setNewPassword(dto.getNewPassword());
        return ResponseEntity.status(HttpStatus.OK).body(changePasswordDTO);
    }



    @PutMapping("/update-fcm-token")
    public ResponseEntity<?> updateFcmToken(@RequestParam String email, @RequestParam String token) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();
        User user = userOpt.get();

        user.setFcmToken(token);
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

}
