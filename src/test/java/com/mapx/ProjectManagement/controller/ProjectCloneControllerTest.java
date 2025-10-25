package com.mapx.ProjectManagement.controller;

import com.mapx.ProjectManagement.controller.ProjectCloneController;
import com.mapx.ProjectManagement.dto.CloneProjectDto.CloneProjectResponse;
import com.mapx.ProjectManagement.dto.CloneProjectDto.ProjectCloneRequest;
import com.mapx.ProjectManagement.service.ProjectCloneService;
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
