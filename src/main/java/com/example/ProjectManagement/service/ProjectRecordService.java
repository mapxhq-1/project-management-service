package com.example.ProjectManagement.service;

import com.example.ProjectManagement.dto.projectDto.DeleteProjectResponse;
import com.example.ProjectManagement.dto.projectDto.GetResponse;
import com.example.ProjectManagement.dto.projectDto.ProjectRequest;
import com.example.ProjectManagement.dto.projectDto.ProjectUpdateRequest;
import com.example.ProjectManagement.dto.projectDto.StatusResponse;
//import com.example.ProjectManagement.model.Notes;
import com.example.ProjectManagement.model.Project;
import com.example.ProjectManagement.repository.ImagesRecordRepository;
import com.example.ProjectManagement.repository.NotesRecordRepository;
import com.example.ProjectManagement.repository.ProjectRecordRepository;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectRecordService {

    private final ProjectRecordRepository repository;
    private final MongoTemplate mongoTemplate;
    private final NotesRecordRepository notesRecordRepository;
    //private final ImagesRecordRepository imagesRecordRepository;

    private static final String COLLECTION_NAME = "project_records";
    private final String notesBasePath = "src/main/resources/html_notes/";
    //private final String imagesBasePath = "/path/to/images/files/";

    public ProjectRecordService(ProjectRecordRepository repository,NotesRecordRepository notesRecordRepository,ImagesRecordRepository imagesRecordRepository,MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
        this.notesRecordRepository = notesRecordRepository;
        //this.imagesRecordRepository = imagesRecordRepository;
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
        if (!StringUtils.hasText(request.getProjectId())) {
            return new StatusResponse("failure", "projectId is missing", null);
        }
        if (!StringUtils.hasText(request.getOwnerEmail())) {
            return new StatusResponse("failure", "ownerEmail is required", null);
        }

        // Check if at least one *valid* updatable field is provided
        boolean hasProjectName = StringUtils.hasText(request.getProjectName());
        boolean hasAccessorList = request.getAccessorList() != null;
        boolean hasProjectConfig = request.getProjectConfig() != null;

        if (!hasProjectName && !hasAccessorList && !hasProjectConfig) {
            return new StatusResponse(
                "failure",
                "At least one updatable field (projectName, accessorList, or projectConfig) must be provided",
                null
            );
        }

        // Fetch the project from DB
        Optional<Project> optionalProject = repository.findById(request.getProjectId());
        if (optionalProject.isEmpty()) {
            return new StatusResponse("failure", "Project not found", null);
        }

        Project project = optionalProject.get();

        // Verify owner
        if (!project.getOwnerEmail().equalsIgnoreCase(request.getOwnerEmail())) {
            return new StatusResponse("failure", "Unauthorized: owner email does not match project owner", null);
        }

        // Apply updates only if non-empty
        if (hasProjectName) {
            project.setProjectName(request.getProjectName().trim());
        }
        if (hasAccessorList) {
            project.setAccessorList(request.getAccessorList());
        }
        if (hasProjectConfig) {
            project.setProjectConfig(request.getProjectConfig());
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
    public DeleteProjectResponse deleteProject(String projectId, String ownerEmail) {

        // 1. Validate projectId format
        if (!ObjectId.isValid(projectId)) {
            return DeleteProjectResponse.failure("Invalid or missing project_id");
        }

        // 2. Find project using String ID (no new ObjectId())
        Optional<Project> projectOpt = repository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return DeleteProjectResponse.failure("Project not found");
        }

        Project project = projectOpt.get();

        // 3. Verify ownership
        if (!project.getOwnerEmail().equalsIgnoreCase(ownerEmail)) {
            return DeleteProjectResponse.failure("Unauthorized: email is not the owner of the project");
        }
        
        // 4. Delete associated notes
        notesRecordRepository.findByProjectId(projectId).forEach(note -> {
            try {
                Files.deleteIfExists(Paths.get(notesBasePath, note.getHtmlFileId() + ".html"));
                notesRecordRepository.delete(note);
            } catch (Exception ignored) {}
        });

        // 5. Delete associated images
        // imagesRecordRepository.findByProjectId(projectId).forEach(img -> {
        //     try {
        //         Files.deleteIfExists(Paths.get(imagesBasePath, img.getImageFileId() + "." + img.getFormat()));
        //         imagesRecordRepository.delete(img);
        //     } catch (Exception ignored) {}
        // });

        // 6. Delete the project
        try {
            repository.delete(project);
        } catch (Exception e) {
            return DeleteProjectResponse.failure("Failed to delete project record");
        }

        return DeleteProjectResponse.success();
    }
}
