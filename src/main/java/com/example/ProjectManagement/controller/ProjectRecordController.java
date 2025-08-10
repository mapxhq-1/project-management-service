package com.example.ProjectManagement.controller;

import com.example.ProjectManagement.dto.CreateProjectRequest;
import com.example.ProjectManagement.dto.CreateProjectResponse;
import com.example.ProjectManagement.service.ProjectRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project-management-service")
public class ProjectRecordController {

    private final ProjectRecordService service;

    public ProjectRecordController(ProjectRecordService service) {
        this.service = service;
    }

    @PostMapping("/create-new-project")
    public ResponseEntity<Object> createNewProject(@RequestBody CreateProjectRequest request) {
        CreateProjectResponse response = service.createNewProject(request);
        if ("failure".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
