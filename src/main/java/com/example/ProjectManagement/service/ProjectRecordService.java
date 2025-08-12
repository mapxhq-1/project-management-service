package com.example.ProjectManagement.service;

import com.example.ProjectManagement.dto.ProjectRequest;
import com.example.ProjectManagement.dto.ProjectUpdateRequest;
import com.example.ProjectManagement.dto.GetResponse;
import com.example.ProjectManagement.model.Project;
import com.example.ProjectManagement.model.StatusResponse;
import com.example.ProjectManagement.repository.ProjectRecordRepository;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectRecordService {

    private final ProjectRecordRepository repository;
    private final MongoTemplate mongoTemplate;
    private static final String COLLECTION_NAME = "project_records";

    public ProjectRecordService(ProjectRecordRepository repository,MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
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
    // 1. Get All Projects Owned by a User
    public ResponseEntity<GetResponse<List<Project>>> getAllProjectsOfOwner(String ownerEmail) {
        if (!StringUtils.hasText(ownerEmail)) {
            return ResponseEntity.badRequest().body(
                    new GetResponse<>("failure", "ownerEmail is required", null)
            );
        }

        Query query = new Query(Criteria.where("ownerEmail").is(ownerEmail));
        List<Project> projects = mongoTemplate.find(query, Project.class, COLLECTION_NAME);

        return ResponseEntity.ok(new GetResponse<>("success", null, projects));
    }

    // 2. Get All Projects Accessible by a User
    public ResponseEntity<GetResponse<List<Project>>> getAllAccessibleProjects(String email) {
        if (!StringUtils.hasText(email)) {
            return ResponseEntity.badRequest().body(
                    new GetResponse<>("failure", "email is required", null)
            );
        }

        Query query = new Query(Criteria.where("accessorList").in(email));
        List<Project> projects = mongoTemplate.find(query, Project.class, COLLECTION_NAME);

        return ResponseEntity.ok(new GetResponse<>("success", null, projects));
    }

    // 3. Get Project by ID
    public ResponseEntity<GetResponse<Project>> getProjectById(String projectId) {
        if (!StringUtils.hasText(projectId)) {
            return ResponseEntity.badRequest().body(
                    new GetResponse<>("failure", "projectId is required", null)
            );
        }

        try {
            Project project = mongoTemplate.findById(new ObjectId(projectId), Project.class, COLLECTION_NAME);
            if (project == null) {
                return ResponseEntity.badRequest().body(
                        new GetResponse<>("failure", "Project not found", null)
                );
            }

            return ResponseEntity.ok(new GetResponse<>("success", null, project));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new GetResponse<>("failure", "Invalid projectId format", null)
            );
        }
    }
}
