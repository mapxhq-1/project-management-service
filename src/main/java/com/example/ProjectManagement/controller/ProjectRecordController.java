package com.example.ProjectManagement.controller;

import com.example.ProjectManagement.model.Project;
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
    public ResponseEntity<StatusResponse> createNewProject(@RequestBody Project project) {
        StatusResponse response = projectRecordService.createNewProject(project);

        if ("failure".equalsIgnoreCase(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }
}
