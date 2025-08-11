package com.example.ProjectManagement.controller;

import com.example.ProjectManagement.dto.ProjectRequest;
import com.example.ProjectManagement.dto.ProjectUpdateRequest;
import com.example.ProjectManagement.model.StatusResponse;
import com.example.ProjectManagement.service.ProjectRecordService;
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

        if ("failure".equalsIgnoreCase(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }
}
