package com.example.ProjectManagement.dto.ImagesDto;

import com.example.ProjectManagement.model.Images;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageGetResponse {
    private String status;
    private String message;
    private Images imageMetadata;
}
