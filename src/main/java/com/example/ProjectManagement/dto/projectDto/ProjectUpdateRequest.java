package com.example.ProjectManagement.dto.projectDto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ProjectUpdateRequest {
    private String projectId;       // Required
    private String projectName;     // Optional
    private List<String> accessorList; // Optional
    private Map<String, Object> projectConfig; // Optional
    private String ownerEmail;      // Required for verification only
}
