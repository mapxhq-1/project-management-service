package com.example.ProjectManagement.controller;

import com.example.ProjectManagement.dto.projectDto.*;
import com.example.ProjectManagement.model.Project;
import com.example.ProjectManagement.service.ProjectRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectRecordControllerTest {

    @Mock
    private ProjectRecordService projectRecordService;

    @InjectMocks
    private ProjectRecordController projectRecordController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // createNewProject - success
    @Test
    void testCreateNewProject_success() {
        ProjectRequest request = new ProjectRequest();
        StatusResponse mockResponse = new StatusResponse("success", null, "proj-1");

        when(projectRecordService.createNewProject(request)).thenReturn(mockResponse);

        ResponseEntity<StatusResponse> response = projectRecordController.createNewProject(request, "mapx");

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().getStatus());
        assertEquals("proj-1", response.getBody().getProjectId());
    }

    // createNewProject - failure
    @Test
    void testCreateNewProject_failure() {
        ProjectRequest request = new ProjectRequest();
        StatusResponse mockResponse = new StatusResponse("failure", "Invalid data", null);

        when(projectRecordService.createNewProject(request)).thenReturn(mockResponse);

        ResponseEntity<StatusResponse> response = projectRecordController.createNewProject(request, "mapx");

        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("failure", response.getBody().getStatus());
        assertEquals("Invalid data", response.getBody().getMessage());
    }

    // updateProject - unauthorized (401)
    @Test
    void testUpdateProject_unauthorized() {
        ProjectUpdateRequest request = new ProjectUpdateRequest();
        StatusResponse mockResponse = new StatusResponse("failure", "Unauthorized: owner email does not match project owner", null);

        when(projectRecordService.updateProject(request)).thenReturn(mockResponse);

        ResponseEntity<StatusResponse> response = projectRecordController.updateProject(request, "mapx");

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Unauthorized: owner email does not match project owner", response.getBody().getMessage());
    }

    // updateProject - project not found (404)
    @Test
    void testUpdateProject_notFound() {
        ProjectUpdateRequest request = new ProjectUpdateRequest();
        StatusResponse mockResponse = new StatusResponse("failure", "Project not found", null);

        when(projectRecordService.updateProject(request)).thenReturn(mockResponse);

        ResponseEntity<StatusResponse> response = projectRecordController.updateProject(request, "mapx");

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Project not found", response.getBody().getMessage());
    }

    // updateProject - bad request (400)
    @Test
    void testUpdateProject_badRequest() {
        ProjectUpdateRequest request = new ProjectUpdateRequest();
        StatusResponse mockResponse = new StatusResponse("failure",
                "At least one updatable field (projectName, accessorList, or projectConfig) must be provided", null);

        when(projectRecordService.updateProject(request)).thenReturn(mockResponse);

        ResponseEntity<StatusResponse> response = projectRecordController.updateProject(request, "mapx");

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("failure", response.getBody().getStatus());
    }

    // updateProject - success (message == null -> 200)
    @Test
    void testUpdateProject_success() {
        ProjectUpdateRequest request = new ProjectUpdateRequest();
        StatusResponse mockResponse = new StatusResponse("success", null, "proj-1");

        when(projectRecordService.updateProject(request)).thenReturn(mockResponse);

        ResponseEntity<StatusResponse> response = projectRecordController.updateProject(request, "mapx");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody().getStatus());
        assertNull(response.getBody().getMessage());
    }

    // getAllProjectsOfOwner
    @Test
    void testGetAllProjectsOfOwner_success() {
        GetResponse<List<Project>> mockResponse = new GetResponse<>("success", null, Collections.emptyList());
        when(projectRecordService.getAllProjectsOfOwner("owner@test.com"))
                .thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<GetResponse<List<Project>>> response =
                projectRecordController.getAllProjectsOfOwner("owner@test.com", "mapx");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody().getStatus());
    }

    // getAllAccessibleProjects
    @Test
    void testGetAllAccessibleProjects_success() {
        GetResponse<List<Project>> mockResponse = new GetResponse<>("success", null, Collections.emptyList());
        when(projectRecordService.getAllAccessibleProjects("user@test.com"))
                .thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<GetResponse<List<Project>>> response =
                projectRecordController.getAllAccessibleProjects("user@test.com", "mapx");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody().getStatus());
    }

    // getProjectById
    @Test
    void testGetProjectById_success() {
        GetResponse<Project> mockResponse = new GetResponse<>("success", null, new Project());
        when(projectRecordService.getProjectById("proj1"))
                .thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<GetResponse<Project>> response =
                projectRecordController.getProjectById("proj1", "mapx");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody().getStatus());
    }

    // deleteProject - success
    @Test
    void testDeleteProject_success() {
        DeleteProjectResponse mockResponse = new DeleteProjectResponse("success", null);
        when(projectRecordService.deleteProject("proj1", "owner@test.com"))
                .thenReturn(mockResponse);

        ResponseEntity<DeleteProjectResponse> response =
                projectRecordController.deleteProject("proj1", "owner@test.com", "mapx");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody().getStatus());
    }

    // deleteProject - unauthorized (401)
    @Test
    void testDeleteProject_unauthorized() {
        DeleteProjectResponse mockResponse =
                new DeleteProjectResponse("failure", "Unauthorized: email is not the owner of the project");

        when(projectRecordService.deleteProject("proj1", "other@test.com"))
                .thenReturn(mockResponse);

        ResponseEntity<DeleteProjectResponse> response =
                projectRecordController.deleteProject("proj1", "other@test.com", "mapx");

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Unauthorized: email is not the owner of the project", response.getBody().getMessage());
    }

    // deleteProject - bad request (400)
    @Test
    void testDeleteProject_badRequest() {
        DeleteProjectResponse mockResponse = new DeleteProjectResponse("failure", "Some error");
        when(projectRecordService.deleteProject("proj1", "owner@test.com"))
                .thenReturn(mockResponse);

        ResponseEntity<DeleteProjectResponse> response =
                projectRecordController.deleteProject("proj1", "owner@test.com", "mapx");

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("failure", response.getBody().getStatus());
    }

    // invalid client name should throw exception
    @Test
    void testInvalidClientName_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> projectRecordController.createNewProject(new ProjectRequest(), "wrongClient"),
                "Invalid client_name. Expected 'mapx'.");
    }
}
