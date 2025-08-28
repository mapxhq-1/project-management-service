package com.example.ProjectManagement.controller;


import com.example.ProjectManagement.dto.CloneProjectDto.CloneProjectResponse;
import com.example.ProjectManagement.dto.CloneProjectDto.ProjectCloneRequest;
import com.example.ProjectManagement.service.ProjectCloneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project-management-service")
public class ProjectCloneController {

    @Autowired
    private ProjectCloneService projectService;

    @PostMapping("/clone-project")

    public ResponseEntity<CloneProjectResponse> CloneProject(
             @RequestParam("email") String email,
             @RequestBody ProjectCloneRequest project
    ){
        String projectId=project.getProjectId();
         CloneProjectResponse response=projectService.CloneProject(email,projectId);
        if ("failure: INVALID_PROJECT_ID".equalsIgnoreCase(response.getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else if ("failure: UNAUTHORIZED_ACCESS".equalsIgnoreCase(response.getStatus())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        return ResponseEntity.ok(response);
    }

}
