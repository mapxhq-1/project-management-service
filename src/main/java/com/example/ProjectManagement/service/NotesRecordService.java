package com.example.ProjectManagement.service;

import com.example.ProjectManagement.dto.NotesDto.*;
import com.example.ProjectManagement.model.HistoricalYear;
import com.example.ProjectManagement.model.Notes;
import com.example.ProjectManagement.model.Project;
import com.example.ProjectManagement.dto.NotesDto.NotesResponse;
import com.example.ProjectManagement.repository.NotesRecordRepository;
import com.example.ProjectManagement.repository.ProjectRecordRepository;
import org.bson.types.ObjectId;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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


    // To check the era
    public boolean checkEra(String era) {
        return "BCE".equals(era) || "CE".equals(era);
    }


    //GET Method to fetch the notes details by noteId
    public GetNoteResponse getNoteById(String noteId) {
        // Validate ID
        if (noteId == null || noteId.isEmpty() || !ObjectId.isValid(noteId)) {
            return new GetNoteResponse("failure", "Invalid or missing note ID", null);
        }

        // Fetch from DB
        Notes note = notesRepository.findById(noteId).orElse(null);
        if (note == null) {
            return new GetNoteResponse("failure", "Note not found", null);
        }

        // Read HTML content
        String noteContent;
        try {
            File file = new File(HTML_NOTES_DIR+ note.getHtmlFileId() + ".html");
            if (!file.exists()) {
                return new GetNoteResponse("failure", "Failed to read note content from disk", null);
            }
            noteContent = Files.readString(file.toPath());
        } catch (Exception e) {
            return new GetNoteResponse("failure", "Failed to read note content from disk", null);
        }

        // Prepare DTO
        NoteResponseDto dto = new NoteResponseDto(
                note.getId(),
                note.getProjectId(),
                note.getLatitude(),
                note.getLongitude(),
                note.getCreatedAt(),
                note.getUpdatedAt(),
                noteContent
        );

        return new GetNoteResponse("success", null, dto);
    }


    //GET request Get Notes by Latitude, Longitude, year_in_timeline, and Project ID
    public GetNoteResponse getNotesByLatLongYear(
            String projectId,
            double latitude,
            double longitude,
            HistoricalYear yearInTimeline
    ) {
        // Validate inputs
        if (projectId == null || projectId.isEmpty()) {
            return new GetNoteResponse("failure", "Invalid or missing projectId", null);
        }
        if (latitude < -90 || latitude > 90) {
            return new GetNoteResponse("failure", "Invalid latitude value", null);
        }
        if (longitude < -180 || longitude > 180) {
            return new GetNoteResponse("failure", "Invalid longitude value", null);
        }
        if (yearInTimeline == null ||
                yearInTimeline.getEra() == null || yearInTimeline.getEra().isEmpty()) {
            return new GetNoteResponse("failure", "Invalid or missing yearInTimeline", null);
        }
        if(!checkEra(yearInTimeline.getEra())){
            return new GetNoteResponse("failure", "Give the correct era BCE or CE", null);
        }

        // Fetch from DB
        List<Notes> notes = notesRepository.findByProjectIdAndLatitudeAndLongitudeAndYearInTimeline(
                projectId, latitude, longitude, yearInTimeline
        );

        if (notes.isEmpty()) {
            return new GetNoteResponse("failure", "No notes found", null);
        }

        // Convert to DTO
        List<GetNoteResponseDto> noteDtos = notes.stream()
                .map(note -> new GetNoteResponseDto(
                        note.getId(),
                        note.getProjectId(),
                        note.getNoteTitle(),
                        note.getLatitude(),
                        note.getLongitude(),
                        note.getYearInTimeline(),
                        note.getHtmlFileId(),
                        note.getCreatedAt(),
                        note.getUpdatedAt()
                ))
                .toList();

        return new GetNoteResponse("success", null, noteDtos);
    }


    //Get All Notes by Project ID
    public GetNoteResponse getAllNotesByProjectId(
            String projectId
    ) {
        if (projectId == null || projectId.isEmpty()) {
            return new GetNoteResponse("failure", "Invalid or missing projectId", null);
        }
        // Fetch from DB
        List<Notes> notes = notesRepository.findByProjectId(
                projectId
        );

        if (notes.isEmpty()) {
            return new GetNoteResponse("failure", "No notes found", null);
        }

        List<GetNoteByProjIdOrProjIdAndYearResponseDto> noteDtos=new ArrayList<>();
        for(Notes note:notes){
            // Read HTML content
            String noteContent;
            try {
                File file = new File(HTML_NOTES_DIR+ note.getHtmlFileId() + ".html");
                if (!file.exists()) {
                    return new GetNoteResponse("failure", "Failed to read note content from disk", null);
                }
                noteContent = Files.readString(file.toPath());
            } catch (Exception e) {
                return new GetNoteResponse("failure", "Failed to read note content from disk", null);
            }
            GetNoteByProjIdOrProjIdAndYearResponseDto dto=new GetNoteByProjIdOrProjIdAndYearResponseDto(
                    note.getId(),
                    note.getProjectId(),
                    note.getNoteTitle(),
                    note.getLatitude(),
                    note.getLongitude(),
                    note.getBackgroundColor(),
                    note.getYearInTimeline(),
                    noteContent,
                    note.getCreatedAt(),
                    note.getUpdatedAt()
            );
            noteDtos.add(dto);

        }
        return new GetNoteResponse("success", null, noteDtos);
    }




    //Get All Notes by Project ID and year
    public GetNoteResponse getAllNotesByProjectIdAndYear(
            String projectId,
            HistoricalYear yearInTimeline
    ) {
        if (projectId == null || projectId.isEmpty()) {
            return new GetNoteResponse("failure", "Invalid or missing projectId", null);
        }
        if (yearInTimeline == null ||
                yearInTimeline.getEra() == null || yearInTimeline.getEra().isEmpty()) {
            return new GetNoteResponse("failure", "Invalid or missing yearInTimeline", null);
        }
        if(!checkEra(yearInTimeline.getEra())){
            return new GetNoteResponse("failure", "Give the correct era BCE or CE", null);
        }
        // Fetch from DB
        List<Notes> notes = notesRepository.findByProjectIdAndYearInTimeline(
                projectId,yearInTimeline
        );

        if (notes.isEmpty()) {
            return new GetNoteResponse("failure", "No notes found", null);
        }

        List<GetNoteByProjIdOrProjIdAndYearResponseDto> noteDtos=new ArrayList<>();
        for(Notes note:notes){
            // Read HTML content
            String noteContent;
            try {
                File file = new File(HTML_NOTES_DIR+ note.getHtmlFileId() + ".html");
                if (!file.exists()) {
                    return new GetNoteResponse("failure", "Failed to read note content from disk", null);
                }
                noteContent = Files.readString(file.toPath());
            } catch (Exception e) {
                return new GetNoteResponse("failure", "Failed to read note content from disk", null);
            }
            GetNoteByProjIdOrProjIdAndYearResponseDto dto=new GetNoteByProjIdOrProjIdAndYearResponseDto(
                    note.getId(),
                    note.getProjectId(),
                    note.getNoteTitle(),
                    note.getLatitude(),
                    note.getLongitude(),
                    note.getBackgroundColor(),
                    note.getYearInTimeline(),
                    noteContent,
                    note.getCreatedAt(),
                    note.getUpdatedAt()
            );
            noteDtos.add(dto);

        }
        return new GetNoteResponse("success", null, noteDtos);
    }


     //create a note service

    public NotesResponse createNewNote(CreateNoteRequest request) {

        // Validate required fields
        if (request.getProjectId() == null || request.getProjectId().trim().isEmpty()) {
            return new NotesResponse("failure", "Project ID is required", null);
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return new NotesResponse("failure", "Email is required", null);
        }
        if (request.getHtmlText() == null || request.getHtmlText().trim().isEmpty()) {
            return new NotesResponse("failure", "HTML content is required", null);
        }
        if (request.getLatitude() < -90 || request.getLatitude() > 90) {
            return new NotesResponse("failure", "Invalid latitude", null);
        }
        if (request.getLongitude() < -180 || request.getLongitude() > 180) {
            return new NotesResponse("failure", "Invalid longitude", null);
        }

        // Check if project exists
        Optional<Project> project = projectRepository.findById(request.getProjectId());
        if (project.isEmpty()) {
            return new NotesResponse("failure", "Project not found", null);
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
            note.setEmail(request.getEmail()); // ✅ use header email
            note.setNoteTitle(request.getNoteTitle());
            note.setYearInTimeline(request.getYearInTimeline());
            note.setLatitude(request.getLatitude());
            note.setLongitude(request.getLongitude());
            note.setHtmlFileId(uuid);
            note.setBackgroundColor(request.getBackgroundColor());
            note.setCreatedAt(Instant.now());
            note.setUpdatedAt(Instant.now());

            Notes savedNote = notesRepository.save(note);

            return new NotesResponse("success", null, savedNote.getId());

        } catch (Exception e) {
            return new NotesResponse("failure", "Error while creating note: " + e.getMessage(), null);
        }
    }

    //logic for update the note
    public NotesResponse updateNote(String noteId,String email,UpdateNoteRequest updateRequest){
        // Validate noteId
        if (noteId == null || noteId.isEmpty() || !ObjectId.isValid(noteId)) {
            return new NotesResponse("failure", "Invalid or missing note ID", null);
        }

        // Validate email
        if (email == null || email.isEmpty()) {
            return new NotesResponse("failure", "Email is required", null);
        }

        // Validate htmlText
        if (updateRequest.getHtmlText() == null || updateRequest.getHtmlText().trim().isEmpty()) {
            return new NotesResponse("failure", "html text must not be null or empty", null);
        }

        // Fetch note
        Notes note = notesRepository.findById(noteId).orElse(null);
        if (note == null) {
            return new NotesResponse("failure", "Note not found", null);
        }

        // Authorization check
        if (!email.equalsIgnoreCase(note.getEmail())) {
            return new NotesResponse("failure", "Unauthorized: email does not match note owner", null);
        }

        // Update HTML file
        try {
            String htmlFileId = note.getHtmlFileId();
            File file = new File(HTML_NOTES_DIR + htmlFileId + ".html");
            try (FileWriter writer = new FileWriter(file, false)) {
                writer.write(updateRequest.getHtmlText());
            }
        } catch (Exception e) {
            return new NotesResponse("failure", "Failed to update note content", null);
        }

        // Update MongoDB record
        try {
            note.setYearInTimeline(updateRequest.getYearInTimeline());
            note.setUpdatedAt(Instant.now());
            notesRepository.save(note);
        } catch (Exception e) {
            return new NotesResponse("failure", "Failed to update note record", null);
        }

        return new NotesResponse("success", null, noteId);

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
