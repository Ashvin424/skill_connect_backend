package com.skillconnect.backend.dtos;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String profileUrl;
}
