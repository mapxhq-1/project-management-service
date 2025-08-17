package com.example.ProjectManagement.service;

import com.example.ProjectManagement.dto.NotesDto.*;
import com.example.ProjectManagement.model.HistoricalYear;
import com.example.ProjectManagement.model.Notes;
import com.example.ProjectManagement.model.Project;
import com.example.ProjectManagement.repository.NotesRecordRepository;
import com.example.ProjectManagement.repository.ProjectRecordRepository;
import com.example.ProjectManagement.service.NotesRecordService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotesRecordServiceTest {

    @Mock
    private NotesRecordRepository notesRepository;

    @Mock
    private ProjectRecordRepository projectRepository;

    @InjectMocks
    private NotesRecordService notesRecordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Test: Invalid noteId
    @Test
    void testGetNoteById_invalidId_returnsFailure() {
        GetNoteResponse response = notesRecordService.getNoteById("invalidId");

        assertEquals("failure", response.getStatus());
        assertEquals("Invalid or missing note ID", response.getMessage());
        assertNull(response.getNote());
    }

    // ✅ Test: Note not found
    @Test
    void testGetNoteById_noteNotFound_returnsFailure() {
        String validId = new ObjectId().toHexString();
        when(notesRepository.findById(validId)).thenReturn(Optional.empty());

        GetNoteResponse response = notesRecordService.getNoteById(validId);

        assertEquals("failure", response.getStatus());
        assertEquals("Note not found", response.getMessage());
    }

    // ✅ Test: Create New Note with missing Project
    @Test
    void testCreateNewNote_projectNotFound_returnsFailure() {
        CreateNoteRequest request = new CreateNoteRequest();
        request.setProjectId("123");
        request.setEmail("test@example.com");
        request.setHtmlText("<p>Test</p>");
        request.setLatitude(10);
        request.setLongitude(20);

        when(projectRepository.findById("123")).thenReturn(Optional.empty());

        NotesResponse response = notesRecordService.createNewNote(request);

        assertEquals("failure", response.getStatus());
        assertEquals("Project not found", response.getMessage());
    }

    // ✅ Test: Delete Note unauthorized user
    @Test
    void testDeleteNoteById_emailMismatch_returnsFailure() {
        String noteId = new ObjectId().toHexString();
        Notes note = new Notes();
        note.setId(noteId);
        note.setEmail("owner@example.com");

        when(notesRepository.findById(noteId)).thenReturn(Optional.of(note));

        Response response = notesRecordService.deleteNoteById(noteId, "hacker@example.com");

        assertEquals("failure", response.getStatus());
        assertEquals("Unauthorized: email does not match note owner", response.getMessage());
    }
}
