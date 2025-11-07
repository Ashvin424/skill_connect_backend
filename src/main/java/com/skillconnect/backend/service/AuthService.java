package com.skillconnect.backend.service;

import com.skillconnect.backend.customException.DuplicateResourceException;
import com.skillconnect.backend.customException.ResourceNotFoundException;
import com.skillconnect.backend.dtos.LoginRequestDTO;
import com.skillconnect.backend.dtos.LoginResponseDTO;
import com.skillconnect.backend.dtos.UserRegistrationDTO;
import com.skillconnect.backend.models.User;
import com.skillconnect.backend.repository.UserRepository;
import com.skillconnect.backend.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    public User register(UserRegistrationDTO dto) {
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicateResourceException("User with this email already exists");
        }
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setLocation(dto.getLocation());
        user.setSkills(dto.getSkills());
        user.setServiceMode(dto.getServiceMode());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userRepository.save(user);
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponseDTO(
                accessToken,
                refreshToken,
                user.getEmail(),
                user.getName(),
                user.getId()
        );
    }
}
