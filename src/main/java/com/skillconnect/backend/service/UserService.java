package com.skillconnect.backend.service;

import com.skillconnect.backend.customException.ResourceNotFoundException;
import com.skillconnect.backend.dtos.ChangePasswordDTO;
import com.skillconnect.backend.dtos.UpdateProfileDTO;
import com.skillconnect.backend.dtos.UpdateUserDTO;
import com.skillconnect.backend.models.User;
import com.skillconnect.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //get user by id for profile page
    public Optional<User> getUserById(Long id){
        return userRepository.findById(id);
    }

    //get user by username
    public Optional<User> getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUser(UpdateUserDTO dto) {
        User existingUser = userRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found" ));
            existingUser.setName(dto.getName());
            existingUser.setUsername(dto.getUsername());
            existingUser.setBio(dto.getBio());
            existingUser.setSkills(dto.getSkills());
            existingUser.setLocation(dto.getLocation());
            existingUser.setProfileImageUrl(dto.getProfileImageUrl());
            existingUser.setServiceMode(dto.getServiceMode());
            // Note: Password is not updated here, as it should be handled separately
            userRepository.save(existingUser);

            return existingUser;

    }

    public User updateProfile(String email, UpdateProfileDTO dto){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getBio() != null) user.setBio(dto.getBio());
        if (dto.getSkills() != null) user.setSkills(dto.getSkills());
        if (dto.getLocation() != null) user.setLocation(dto.getLocation());
        if (dto.getProfileImageUrl() != null) user.setProfileImageUrl(dto.getProfileImageUrl());
        if (dto.getServiceMode() != null) user.setServiceMode(dto.getServiceMode());

        return userRepository.save(user);
    }

    /*
        # UserService Enhancements
            - `save(User user)` method allows direct saving of the user entity.
            - Required when:
                    - Updating profile image
                    - Handling password reset
                    - Any direct field changes outside of DTOs

        Method: save(User user)
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    public String uploadProfileImage(String email, MultipartFile imageFile) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        String contentType = imageFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed.");
        }

        String fileUrl = cloudinaryService.uploadFile(imageFile);

        user.setProfileImageUrl(fileUrl);
        userRepository.save(user);

        return fileUrl;
    }


    public void changePassword(String email, ChangePasswordDTO dto){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())){
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        if (dto.getCurrentPassword().equals(dto.getNewPassword())){
            throw new IllegalArgumentException("New password cannot be the same as current password.");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    // Update FCM Token Method
    // This method allows updating the Firebase Cloud Messaging (FCM) token for a user.
    // It is useful for sending push notifications to the user's device.
    public void updateFcmToken(String email, String fcmToken) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        user.setFcmToken(fcmToken);
        userRepository.save(user);
    }

    public void clearFcmToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
        user.setFcmToken(null); // clear token
        userRepository.save(user);
    }
}
