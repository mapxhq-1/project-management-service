package com.example.ProjectManagement.service;

import com.example.ProjectManagement.dto.ProjectRequest;
import com.example.ProjectManagement.model.Project;
import com.example.ProjectManagement.model.StatusResponse;
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

    public StatusResponse createNewProject(ProjectRequest request) {
        // Validate
        if (!StringUtils.hasText(request.getOwnerEmail())) {
            return new StatusResponse("failure", "ownerEmail is required", null);
        }
        if (!StringUtils.hasText(request.getProjectName())) {
            return new StatusResponse("failure", "projectName is required", null);
        }

        // Normalize defaults
        if (request.getAccessorList() == null) {
            request.setAccessorList(Collections.emptyList());
        }
        if (request.getProjectConfig() == null) {
            request.setProjectConfig(new HashMap<>());
        }

        // Create and save project
        Project saved = repository.save(Project.builder()
                .ownerEmail(request.getOwnerEmail())
                .accessorList(request.getAccessorList())
                .projectName(request.getProjectName())
                .projectConfig(request.getProjectConfig())
                .build()
        );

        // Return success response
        return new StatusResponse("success", null, saved.getId());
    }
}
