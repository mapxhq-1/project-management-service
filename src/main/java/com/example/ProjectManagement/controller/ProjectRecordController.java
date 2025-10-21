package com.example.ProjectManagement.controller;

import com.example.ProjectManagement.dto.projectDto.DeleteProjectResponse;
import com.example.ProjectManagement.dto.projectDto.GetResponse;
import com.example.ProjectManagement.dto.projectDto.ProjectRequest;
import com.example.ProjectManagement.dto.projectDto.ProjectUpdateRequest;
import com.example.ProjectManagement.dto.projectDto.StatusResponse;
import com.example.ProjectManagement.model.Project;
import com.example.ProjectManagement.service.ProjectRecordService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project-management-service")
public class ProjectRecordController {

    private final ProjectRecordService projectRecordService;

    // ✅ Utility method to enforce client_name check
    private void validateClientName(String clientName) {
        if (!"mapx".equalsIgnoreCase(clientName)) {
                throw new IllegalArgumentException("Invalid client_name. Expected 'mapx'.");
        }
    }



    public ProjectRecordController(ProjectRecordService projectRecordService) {
        this.projectRecordService = projectRecordService;
    }

    @PostMapping("/create-new-project")
    public ResponseEntity<StatusResponse> createNewProject(
            @RequestBody ProjectRequest project,
            @RequestHeader("client_name") String clientName
    ) {
         validateClientName(clientName);
        StatusResponse response = projectRecordService.createNewProject(project);

        if ("failure".equalsIgnoreCase(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/update-project")
    public ResponseEntity<StatusResponse> updateProject(
            @RequestBody ProjectUpdateRequest request,
            @RequestHeader("client_name") String clientName
    ) {
        validateClientName(clientName);
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
            @RequestParam String ownerEmail,
            @RequestHeader("client_name") String clientName
            ){
        validateClientName(clientName);
        return projectRecordService.getAllProjectsOfOwner(ownerEmail);
    }

    // 2. Get All Projects Accessible by a User
    @GetMapping("/get-all-accessible-projects")
    public ResponseEntity<GetResponse<List<Project>>> getAllAccessibleProjects(
            @RequestParam String email,
            @RequestHeader("client_name") String clientName) {

        validateClientName(clientName);
        return projectRecordService.getAllAccessibleProjects(email);
    }

    // 3. Get Project by ID
    @GetMapping("/get-project-by-id/{projectId}")
    public ResponseEntity<GetResponse<Project>> getProjectById(
            @PathVariable String projectId,
            @RequestHeader("client_name") String clientName) {
        validateClientName(clientName);
        return projectRecordService.getProjectById(projectId);
    }
    @DeleteMapping("/delete-project/{projectId}")
    public ResponseEntity<DeleteProjectResponse> deleteProject(
            @PathVariable("projectId") String projectId,
            @RequestParam("ownerEmail") String ownerEmail,
            @RequestHeader("client_name") String clientName) {

       validateClientName(clientName);
        DeleteProjectResponse response = projectRecordService.deleteProject(projectId, ownerEmail);

        if ("success".equals(response.getStatus())) {
            return ResponseEntity.ok(response);
        }else if ("Unauthorized: email is not the owner of the project".equals(response.getMessage())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } 
        else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
