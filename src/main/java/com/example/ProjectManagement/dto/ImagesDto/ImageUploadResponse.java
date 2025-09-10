package com.example.ProjectManagement.dto.ImagesDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageUploadResponse {
    private String status;
    private String message;
    private String imageId;
}
