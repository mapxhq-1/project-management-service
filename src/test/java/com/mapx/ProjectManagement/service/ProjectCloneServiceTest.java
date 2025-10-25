package com.mapx.ProjectManagement.service;

import com.mapx.ProjectManagement.dto.CloneProjectDto.CloneProjectResponse;
import com.mapx.ProjectManagement.Exception.InvalidProjectIdException;
import com.mapx.ProjectManagement.model.Project;
import com.mapx.ProjectManagement.repository.HyperlinkRecordRepository;
import com.mapx.ProjectManagement.repository.ImagesRecordRepository;
import com.mapx.ProjectManagement.repository.NotesRecordRepository;
import com.mapx.ProjectManagement.repository.ProjectRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapx.ProjectManagement.service.ProjectCloneService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectCloneServiceTest {

    @Mock private ProjectRecordRepository projectRepository;
    @Mock private NotesRecordRepository notesRepository;
    @Mock private ImagesRecordRepository imagesRepository;
    @Mock private HyperlinkRecordRepository hyperlinkRepository;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks private ProjectCloneService service;

    private Project originalProject;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        originalProject = new Project();
        originalProject.setId(new ObjectId().toHexString());
        originalProject.setOwnerEmail("owner@test.com");
        originalProject.setAccessorList(List.of("collab@test.com"));
    }

    @Test
    void cloneProject_invalidId_returnsFailure() {
        CloneProjectResponse res = service.CloneProject("user@test.com", "invalidId");
        assertEquals("failure: INVALID_PROJECT_ID", res.getStatus());
        assertNull(res.getProjectId());
    }

    @Test
    void cloneProject_projectNotFound_throwsException() {
        when(projectRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(InvalidProjectIdException.class,
                () -> service.CloneProject("user@test.com", originalProject.getId()));
    }


    @Test
    void cloneProject_fileCopyError_returnsFailure() throws Exception {
        String projectId = "507f1f77bcf86cd799439011";

        Project project = new Project();
        project.setId(projectId);
        project.setOwnerEmail("owner@test.com");
        project.setAccessorList(Collections.emptyList());

        // Mock repo responses
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(objectMapper.convertValue(any(Project.class), eq(Project.class)))
                .thenReturn(new Project());

        Project saved = new Project();
        saved.setId("cloned123");
        when(projectRepository.save(any(Project.class))).thenReturn(saved);

        // Spy the service so we can override cloneNotes
        ProjectCloneService spyService = Mockito.spy(service);
        doThrow(new IOException("Simulated file copy error"))
                .when(spyService).cloneNotes(anyString(), anyString(), anyString());

        // Call the method
        CloneProjectResponse res = spyService.CloneProject("owner@test.com", projectId);

        // Verify
        assertEquals("failure: FILE_COPY_ERROR", res.getStatus());
        assertNull(res.getProjectId());
    }

    @Test
    void cloneProject_internalError_returnsFailure() throws Exception {
        when(projectRepository.findById(originalProject.getId()))
                .thenReturn(Optional.of(originalProject));
        when(objectMapper.convertValue(any(Project.class), eq(Project.class)))
                .thenThrow(new RuntimeException("boom"));

        CloneProjectResponse res = service.CloneProject("owner@test.com", originalProject.getId());

        assertEquals("failure: INTERNAL_ERROR", res.getStatus());
        assertNull(res.getProjectId());
    }
}
