package com.example.ProjectManagement.service;

import com.example.ProjectManagement.dto.ProjectRequest;
import com.example.ProjectManagement.dto.ProjectUpdateRequest;
import com.example.ProjectManagement.model.Project;
import com.example.ProjectManagement.model.StatusResponse;
import com.example.ProjectManagement.repository.ProjectRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

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
    public StatusResponse updateProject(ProjectUpdateRequest request) {
        // Validate required fields
        if (!StringUtils.hasText(request.getFileId())) {
            return new StatusResponse("failure", "fileId is missing", null);
        }
        if (!StringUtils.hasText(request.getOwnerEmail())) {
            return new StatusResponse("failure", "ownerEmail is required", null);
        }

        // Check if at least one updatable field is provided
        if (request.getProjectName() == null &&
            request.getAccessorList() == null &&
            request.getProjectConfig() == null) {
            return new StatusResponse("failure", 
                "At least one updatable field (projectName, accessorList, or projectConfig) must be provided", 
                null);
        }

        // Fetch the project from DB
        Optional<Project> optionalProject = repository.findById(request.getFileId());
        if (optionalProject.isEmpty()) {
            return new StatusResponse("failure", "Project not found", null);
        }

        Project project = optionalProject.get();

        // Verify owner
        if (!project.getOwnerEmail().equalsIgnoreCase(request.getOwnerEmail())) {
            return new StatusResponse("failure", "Unauthorized: owner email does not match project owner", null);
        }

        // Apply updates
        if (request.getProjectName() != null) {
            project.setProjectName(request.getProjectName());
        }
        if (request.getAccessorList() != null) {
            project.setAccessorList(request.getAccessorList());
        }
        if (request.getProjectConfig() != null) {
            project.setProjectConfig(request.getProjectConfig()); // Replace whole object
        }

        project.setUpdatedAt(Instant.now());

        // Save updated project
        Project saved = repository.save(project);

        return new StatusResponse("success", null, saved.getId());
    }
}
