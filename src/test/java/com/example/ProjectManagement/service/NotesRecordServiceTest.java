package com.example.ProjectManagement.service;

import com.example.ProjectManagement.dto.NotesDto.*;
import com.example.ProjectManagement.model.Notes;
import com.example.ProjectManagement.repository.NotesRecordRepository;
import com.example.ProjectManagement.repository.ProjectRecordRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.time.Instant;
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

    @TempDir
    Path tempDir;  // creates a temporary test directory for files

    private Notes buildValidNote(String noteId) {
        Notes note = new Notes();
        note.setId(noteId);
        note.setHtmlFileId("test-file");
        note.setLatitude(12.34);
        note.setLongitude(56.78);
        note.setProjectId("proj1");
        note.setEmail("test@example.com");
        note.setCreatedAt(Instant.now());
        note.setUpdatedAt(Instant.now());
        return note;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 1️⃣ Invalid noteId
    @Test
    void testGetNoteById_invalidId_returnsFailure() {
        GetNoteResponse response = notesRecordService.getNoteById("invalidId");

        assertEquals("failure", response.getStatus());
        assertEquals("Invalid or missing note ID", response.getMessage());
        assertNull(response.getNote());
    }

    // 2️⃣ Valid id but note not found
    @Test
    void testGetNoteById_noteNotFound_returnsFailure() {
        String noteId = new ObjectId().toString();
        when(notesRepository.findById(noteId)).thenReturn(Optional.empty());

        GetNoteResponse response = notesRecordService.getNoteById(noteId);

        assertEquals("failure", response.getStatus());
        assertEquals("Note not found", response.getMessage());
        assertNull(response.getNote());
    }

    // 3️⃣ Note found but HTML file missing
    @Test
    void testGetNoteById_fileReadError_returnsFailure() throws Exception {
        String noteId = new ObjectId().toHexString();
        Notes note = buildValidNote(noteId);

        // ensure file does NOT exist so read fails
        File file = new File("src/main/resources/html_notes/test-file.html");
        if (file.exists()) file.delete();

        when(notesRepository.findById(noteId)).thenReturn(Optional.of(note));

        GetNoteResponse response = notesRecordService.getNoteById(noteId);

        assertEquals("failure", response.getStatus());
        assertEquals("Failed to read note content from disk", response.getMessage());
        assertNull(response.getNote());
    }

    @Test
    void testGetNoteById_success_returnsNote() throws Exception {
        String noteId = new ObjectId().toHexString();
        Notes note = buildValidNote(noteId);

        // create a fake HTML file
        File file = new File("src/main/resources/html_notes/test-file.html");
        file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("<h1>Test Note</h1>");
        }

        when(notesRepository.findById(noteId)).thenReturn(Optional.of(note));

        GetNoteResponse response = notesRecordService.getNoteById(noteId);

        assertEquals("success", response.getStatus());
        assertNotNull(response.getNote());
        NoteResponseDto noteDto = (NoteResponseDto) response.getNote();

        assertEquals(noteId, noteDto.getNoteId());
        assertTrue(noteDto.getNoteContent().contains("Test Note"));
        file.delete();
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
