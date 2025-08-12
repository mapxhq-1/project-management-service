package com.example.ProjectManagement.service;

import com.example.ProjectManagement.dto.CreateNoteRequest;
import com.example.ProjectManagement.dto.UpdateNoteRequest;
import com.example.ProjectManagement.model.Notes;
import com.example.ProjectManagement.model.Project;
import com.example.ProjectManagement.model.StatusResponse;
import com.example.ProjectManagement.dto.Response;
import com.example.ProjectManagement.repository.NotesRecordRepository;
import com.example.ProjectManagement.repository.ProjectRecordRepository;
import org.bson.types.ObjectId;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotesRecordService {

    private static final String HTML_NOTES_DIR = "src/main/resources/html_notes/";

    @Autowired
    private ProjectRecordRepository projectRepository;

    @Autowired
    private NotesRecordRepository notesRepository;


    //GET Method to fetch the notes details by noteId








    public StatusResponse createNewNote(CreateNoteRequest request) {

        // Validate required fields
        if (request.getProjectId() == null || request.getProjectId().trim().isEmpty()) {
            return new StatusResponse("failure", "Project ID is required", null);
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return new StatusResponse("failure", "Email is required", null);
        }
        if (request.getHtmlText() == null || request.getHtmlText().trim().isEmpty()) {
            return new StatusResponse("failure", "HTML content is required", null);
        }
        if (request.getLatitude() < -90 || request.getLatitude() > 90) {
            return new StatusResponse("failure", "Invalid latitude", null);
        }
        if (request.getLongitude() < -180 || request.getLongitude() > 180) {
            return new StatusResponse("failure", "Invalid longitude", null);
        }

        // Check if project exists
        Optional<Project> project = projectRepository.findById(request.getProjectId());
        if (project.isEmpty()) {
            return new StatusResponse("failure", "Project not found", null);
        }

        try {
            // Create HTML file
            String uuid = UUID.randomUUID().toString();
            File dir = new File(HTML_NOTES_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(HTML_NOTES_DIR + uuid + ".html");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(request.getHtmlText());
            }

            // Save to DB
            Notes note = new Notes();
            note.setProjectId(request.getProjectId());
            note.setEmail(request.getEmail());
            note.setNoteTitle(request.getNoteTitle());
            note.setYearInTimeline(request.getYearInTimeline());
            note.setLatitude(request.getLatitude());
            note.setLongitude(request.getLongitude());
            note.setHtmlFileId(uuid);
            note.setCreatedAt(Instant.now());
            note.setUpdatedAt(Instant.now());

            Notes savedNote = notesRepository.save(note);

            return new StatusResponse("success", null, savedNote.getId());

        } catch (Exception e) {
            return new StatusResponse("failure", "Error while creating note: " + e.getMessage(), null);
        }
    }

    //logic for update the note
    public StatusResponse updateNote(String noteId,String email,UpdateNoteRequest updateRequest){
        // Validate noteId
        if (noteId == null || noteId.isEmpty() || !ObjectId.isValid(noteId)) {
            return new StatusResponse("failure", "Invalid or missing note ID", null);
        }

        // Validate email
        if (email == null || email.isEmpty()) {
            return new StatusResponse("failure", "Email is required", null);
        }

        // Validate htmlText
        if (updateRequest.getHtmlText() == null || updateRequest.getHtmlText().trim().isEmpty()) {
            return new StatusResponse("failure", "html text must not be null or empty", null);
        }

        // Fetch note
        Notes note = notesRepository.findById(noteId).orElse(null);
        if (note == null) {
            return new StatusResponse("failure", "Note not found", null);
        }

        // Authorization check
        if (!email.equalsIgnoreCase(note.getEmail())) {
            return new StatusResponse("failure", "Unauthorized: email does not match note owner", null);
        }

        // Update HTML file
        try {
            String htmlFileId = note.getHtmlFileId();
            File file = new File(HTML_NOTES_DIR + htmlFileId + ".html");
            try (FileWriter writer = new FileWriter(file, false)) {
                writer.write(updateRequest.getHtmlText());
            }
        } catch (Exception e) {
            return new StatusResponse("failure", "Failed to update note content", null);
        }

        // Update MongoDB record
        try {
            note.setYearInTimeline(updateRequest.getYearInTimeline());
            note.setUpdatedAt(Instant.now());
            notesRepository.save(note);
        } catch (Exception e) {
            return new StatusResponse("failure", "Failed to update note record", null);
        }

        return new StatusResponse("success", null, noteId);

    }








    //logic for delete
    public Response deleteNoteById(String noteId, String email) {
        // Validate noteId
        if (noteId == null || noteId.isEmpty() || !ObjectId.isValid(noteId)) {
            return new Response("failure", "Invalid or missing note ID");
        }

        // Validate email
        if (email == null || email.isEmpty()) {
            return new Response("failure", "Email is required");
        }

        // Fetch note
        Notes note = notesRepository.findById(noteId).orElse(null);
        if (note == null) {
            return new Response("failure", "Note not found");
        }

        // Check authorization
        if (!email.equalsIgnoreCase(note.getEmail())) {
            return new Response("failure", "Unauthorized: email does not match note owner");
        }

        // Delete HTML file (best effort)
        try {
            String htmlFileId = note.getHtmlFileId();
            if (htmlFileId != null && !htmlFileId.isEmpty()) {
                // Store outside the JAR so deletion works
                File file=new File(HTML_NOTES_DIR+htmlFileId+".html");
                if(file.exists()){
                    if(!file.delete()){
                        System.err.println("warning: Failed to delete file: "+file.getAbsolutePath());
                    }
                    else{
                        System.out.println("Note: File not found during delete operation: " + file.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error deleting HTML file: " + e.getMessage());
        }

        // Delete MongoDB record
        try {
            notesRepository.deleteById(noteId);
            return new Response("success", null);
        } catch (Exception e) {
            return new Response("failure", "Failed to delete note record");
        }
    }



}
