package com.example.ProjectManagement.controller;

import com.example.ProjectManagement.dto.ProjectRequest;
import com.example.ProjectManagement.dto.ProjectUpdateRequest;
import com.example.ProjectManagement.model.Project;
import com.example.ProjectManagement.model.StatusResponse;
import com.example.ProjectManagement.service.ProjectRecordService;
import com.example.ProjectManagement.dto.GetResponse;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project-management-service")
public class ProjectRecordController {

    private final ProjectRecordService projectRecordService;

    public ProjectRecordController(ProjectRecordService projectRecordService) {
        this.projectRecordService = projectRecordService;
    }

    @PostMapping("/create-new-project")
    public ResponseEntity<StatusResponse> createNewProject(@RequestBody ProjectRequest project) {
        StatusResponse response = projectRecordService.createNewProject(project);

        if ("failure".equalsIgnoreCase(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/update-project")
    public ResponseEntity<StatusResponse> updateProject(@RequestBody ProjectUpdateRequest request) {
        StatusResponse response = projectRecordService.updateProject(request);
        if (response.getMessage()==null){
            return ResponseEntity.ok(response);
        }
        switch (response.getMessage()) {
            case "Unauthorized: owner email does not match project owner":
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // 401

            case "Project not found":
            case "Invalid fileId":
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404

            case "At least one updatable field (projectName, accessorList, or projectConfig) must be provided":
            case "fileId is missing":
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 400

            default:
                return ResponseEntity.ok(response); // 200
        }
    }
     @GetMapping("/get-all-projects-of-owner")
    public ResponseEntity<GetResponse<List<Project>>> getAllProjectsOfOwner(
            @RequestParam String ownerEmail) {
        return projectRecordService.getAllProjectsOfOwner(ownerEmail);
    }

    // 2. Get All Projects Accessible by a User
    @GetMapping("/get-all-accessible-projects")
    public ResponseEntity<GetResponse<List<Project>>> getAllAccessibleProjects(
            @RequestParam String email) {
        return projectRecordService.getAllAccessibleProjects(email);
    }

    // 3. Get Project by ID
    @GetMapping("/get-project-by-id/{projectId}")
    public ResponseEntity<GetResponse<Project>> getProjectById(
            @PathVariable String projectId) {
        return projectRecordService.getProjectById(projectId);
    }
}
