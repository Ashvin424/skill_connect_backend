package com.skillconnect.backend.dtos;

import com.skillconnect.backend.models.Service;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponseDTO {
    private String displayUsername;
    private String name;
    private String email;
    private String bio;
    private String location;
    private String skills;
    private String profileImageUrl;
    private List<Service> services;
    private LocalDateTime createdAt;
    private int skillCount;
    private int serviceCount;
    private int reviewCount;
    private double averageRating;
}
