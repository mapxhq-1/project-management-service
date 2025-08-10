package com.example.ProjectManagement.service;

import com.example.ProjectManagement.dto.CreateProjectRequest;
import com.example.ProjectManagement.dto.CreateProjectResponse;
import com.example.ProjectManagement.model.Project;
import com.example.ProjectManagement.repository.ProjectRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;

@Service
public class ProjectRecordService {

    private final ProjectRecordRepository repository;

    public ProjectRecordService(ProjectRecordRepository repository) {
        this.repository = repository;
    }

    public CreateProjectResponse createNewProject(CreateProjectRequest request) {
        // Validate
        if (!StringUtils.hasText(request.getOwnerEmail())) {
            return new CreateProjectResponse("failure", "ownerEmail is required", null);
        }
        if (!StringUtils.hasText(request.getProjectName())) {
            return new CreateProjectResponse("failure", "projectName is required", null);
        }

        // Normalize
        if (request.getAccessorList() == null) {
            request.setAccessorList(Collections.emptyList());
        }
        if (request.getProjectConfig() == null) {
            request.setProjectConfig(new HashMap<>());
        }

        // Build entity
        Project project = Project.builder()
                .ownerEmail(request.getOwnerEmail())
                .accessorList(request.getAccessorList())
                .projectName(request.getProjectName())
                .projectConfig(request.getProjectConfig())
                .build();

        // Save to MongoDB
        Project saved = repository.save(project);

        // Return response
        return new CreateProjectResponse("success", null, saved.getId());
    }
}
