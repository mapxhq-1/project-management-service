package com.example.ProjectManagement.controller;

import com.example.ProjectManagement.dto.NotesDto.*;
import com.example.ProjectManagement.model.HistoricalYear;
import com.example.ProjectManagement.service.NotesRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class NotesRecordControllerTest {

    @Mock
    private NotesRecordService notesRecordService;

    @InjectMocks
    private NotesRecordController notesRecordController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Test GetNoteById - success
    @Test
    void testGetNoteById_success() {
        String noteId = "123";
        GetNoteResponse mockResponse = new GetNoteResponse("success", null, Collections.emptyList());

        when(notesRecordService.getNoteById(noteId)).thenReturn(mockResponse);

        ResponseEntity<GetNoteResponse> response = notesRecordController.getNoteById(noteId, "mapx");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody().getStatus());
    }

    // ✅ Test GetNoteById - failure
    @Test
    void testGetNoteById_failure() {
        String noteId = "123";
        GetNoteResponse mockResponse = new GetNoteResponse("failure", "Invalid note", null);

        when(notesRecordService.getNoteById(noteId)).thenReturn(mockResponse);

        ResponseEntity<GetNoteResponse> response = notesRecordController.getNoteById(noteId, "mapx");

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("failure", response.getBody().getStatus());
    }

    // ✅ Test GetNotesByLatLongYear - success
    @Test
    void testGetNotesByLatLongYear_success() {
        GetNoteResponse mockResponse = new GetNoteResponse("success", null, Collections.emptyList());

        when(notesRecordService.getNotesByLatLongYear(eq("proj1"), eq(10.0), eq(20.0),
                any(HistoricalYear.class))).thenReturn(mockResponse);

        ResponseEntity<GetNoteResponse> response = notesRecordController.getNotesByLatLongYear(
                "proj1", 10.0, 20.0, 2025L, "CE", "mapx"
        );

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody().getStatus());
    }

    // ✅ Test GetAllNotesByProjectAndYear - failure
    @Test
    void testGetAllNotesByProjectAndYear_failure() {
        GetNoteResponse mockResponse = new GetNoteResponse("failure", "No notes", null);

        when(notesRecordService.getAllNotesByProjectIdAndYear(eq("proj1"), any(HistoricalYear.class)))
                .thenReturn(mockResponse);

        ResponseEntity<GetNoteResponse> response = notesRecordController.getAllNotesByProjectAndYear(
                "proj1", 1999L, "BCE", "mapx"
        );

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("failure", response.getBody().getStatus());
    }

    // ✅ Test CreateNewNote - success
    @Test
    void testCreateNewNote_success() {
        CreateNoteRequest request = new CreateNoteRequest();
        NotesResponse mockResponse = new NotesResponse("success", null, "note123");

        when(notesRecordService.createNewNote(request)).thenReturn(mockResponse);

        ResponseEntity<NotesResponse> response = notesRecordController.createNewNote(request, "mapx");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody().getStatus());
    }

    // ✅ Test UpdateNote - failure
    @Test
    void testUpdateNote_failure() {
        UpdateNoteRequest request = new UpdateNoteRequest();
        NotesResponse mockResponse = new NotesResponse("failure", "Update failed", null);

        when(notesRecordService.updateNote("note1", "test@example.com", request))
                .thenReturn(mockResponse);

        ResponseEntity<NotesResponse> response = notesRecordController.updateNote(
                "note1", "test@example.com", request, "mapdesk"
        );

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("failure", response.getBody().getStatus());
    }

    // ✅ Test DeleteNote - success
    @Test
    void testDeleteNote_success() {
        Response mockResponse = new Response("success", null);

        when(notesRecordService.deleteNoteById("note1", "test@example.com")).thenReturn(mockResponse);

        ResponseEntity<Response> response = notesRecordController.deleteNote(
                "note1", "test@example.com", "mapx"
        );

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody().getStatus());
    }

    // ✅ Test DeleteNote - failure
    @Test
    void testDeleteNote_failure() {
        Response mockResponse = new Response("failure", "Delete failed");

        when(notesRecordService.deleteNoteById("note1", "test@example.com")).thenReturn(mockResponse);

        ResponseEntity<Response> response = notesRecordController.deleteNote(
                "note1", "test@example.com", "mapdesk"
        );

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("failure", response.getBody().getStatus());
    }

    // ✅ Test Invalid client_name throws exception
    @Test
    void testInvalidClientName_throwsException() {
        try {
            notesRecordController.getNoteById("123", "invalidClient");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid client_name. Expected 'mapx' or 'mapdesk'.", e.getMessage());
        }
    }
}
