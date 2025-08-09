package com.skillconnect.backend.dtos;

import lombok.Data;

import java.util.List;

@Data
public class UpdateServiceDTO {
    private String title;
    private String description;
    private String category;
    private List<String> imageUrls;
}
