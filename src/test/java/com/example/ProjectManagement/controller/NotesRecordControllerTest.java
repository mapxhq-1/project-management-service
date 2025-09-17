package com.example.ProjectManagement.controller;

import com.example.ProjectManagement.dto.NotesDto.*;
import com.example.ProjectManagement.service.NotesRecordService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class NotesRecordControllerTest {

    @Mock
    private NotesRecordService notesRecordService;

    @InjectMocks
    private NotesRecordController notesRecordController;

    public NotesRecordControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Test GetNoteById - failure
    @Test
    void testGetNoteById_failure() {
        String noteId = "123";
        GetNoteResponse mockResponse = new GetNoteResponse("failure", "Invalid or missing note ID", null);
        when(notesRecordService.getNoteById(noteId)).thenReturn(mockResponse);

        ResponseEntity<GetNoteResponse> response = notesRecordController.getNoteById(noteId,"mapx");

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("failure", response.getBody().getStatus());
    }

    // ✅ Test CreateNewNote - success
    @Test
    void testCreateNewNote_success() {
        CreateNoteRequest request = new CreateNoteRequest();
        NotesResponse mockResponse = new NotesResponse("success", null, "note123");

        when(notesRecordService.createNewNote(request)).thenReturn(mockResponse);

        ResponseEntity<NotesResponse> response = notesRecordController.createNewNote(request,"mapx");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody().getStatus());
    }
}
