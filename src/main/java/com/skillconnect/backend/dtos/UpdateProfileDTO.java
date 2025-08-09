package com.skillconnect.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileDTO {

    private String name;
    private String bio;
    private String location;
    private String skills; // comma-separated list of skills
    private String profileImageUrl;
    private String serviceMode; // this could be "online", "offline", or "both"
    private String username;

}
