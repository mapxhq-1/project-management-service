package com.example.ProjectManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateProjectResponse {
    private String status;     // "success" or "failure"
    private String message;    // null for success
    private String projectId;  // MongoDB ObjectId string
}
