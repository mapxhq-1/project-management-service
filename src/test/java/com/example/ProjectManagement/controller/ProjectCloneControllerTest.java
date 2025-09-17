package com.example.ProjectManagement.controller;

import com.example.ProjectManagement.dto.CloneProjectDto.CloneProjectResponse;
import com.example.ProjectManagement.dto.CloneProjectDto.ProjectCloneRequest;
import com.example.ProjectManagement.service.ProjectCloneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProjectCloneControllerTest {

    @Mock
    private ProjectCloneService projectService;

    @InjectMocks
    private ProjectCloneController projectCloneController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCloneProject_Success() {
        // Arrange
        String email = "test@example.com";
        ProjectCloneRequest request = new ProjectCloneRequest();
        request.setProjectId("123");

        CloneProjectResponse mockResponse = new CloneProjectResponse();
        mockResponse.setStatus("success");

        when(projectService.CloneProject(email, "123")).thenReturn(mockResponse);

        // Act
        ResponseEntity<CloneProjectResponse> response = projectCloneController.CloneProject(email, request,"mapx");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().getStatus());
        verify(projectService, times(1)).CloneProject(email, "123");
    }

    @Test
    void testCloneProject_UnauthorizedAccess() {
        // Arrange
        String email = "unauthorized@example.com";
        ProjectCloneRequest request = new ProjectCloneRequest();
        request.setProjectId("456");

        CloneProjectResponse mockResponse = new CloneProjectResponse();
        mockResponse.setStatus("failure: UNAUTHORIZED_ACCESS");

        when(projectService.CloneProject(email, "456")).thenReturn(mockResponse);

        // Act
        ResponseEntity<CloneProjectResponse> response = projectCloneController.CloneProject(email, request,"mapx");

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("failure: UNAUTHORIZED_ACCESS", response.getBody().getStatus());
        verify(projectService, times(1)).CloneProject(email, "456");
    }

    @Test
    void testCloneProject_GenericFailure() {
        // Arrange
        String email = "user@example.com";
        ProjectCloneRequest request = new ProjectCloneRequest();
        request.setProjectId("789");

        CloneProjectResponse mockResponse = new CloneProjectResponse();
        mockResponse.setStatus("failure: SOME_OTHER_REASON");

        when(projectService.CloneProject(email, "789")).thenReturn(mockResponse);

        // Act
        ResponseEntity<CloneProjectResponse> response = projectCloneController.CloneProject(email, request,"mapx");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode()); // defaults to 200
        assertEquals("failure: SOME_OTHER_REASON", response.getBody().getStatus());
        verify(projectService, times(1)).CloneProject(email, "789");
    }
}
