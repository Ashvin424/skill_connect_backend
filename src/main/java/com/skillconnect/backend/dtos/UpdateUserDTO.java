package com.skillconnect.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {
    private Long id;
    private String name;
    private String username;
    private String bio;
    private String location;
    private String skills; // comma-separated list of skills
    private String profileImageUrl;
    private String serviceMode; // this could be "online", "offline", or "both"
}
