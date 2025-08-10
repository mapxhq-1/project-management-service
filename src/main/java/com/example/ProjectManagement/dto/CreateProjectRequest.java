package com.example.ProjectManagement.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class CreateProjectRequest {
    private String ownerEmail;
    private List<String> accessorList;
    private String projectName;
    private Map<String, Object> projectConfig;
}
