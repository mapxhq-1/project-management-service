package com.example.ProjectManagement.controller;


import com.example.ProjectManagement.dto.NotesDto.CreateNoteRequest;
import com.example.ProjectManagement.dto.NotesDto.GetNoteResponse;
import com.example.ProjectManagement.dto.NotesDto.Response;
import com.example.ProjectManagement.dto.NotesDto.UpdateNoteRequest;
import com.example.ProjectManagement.model.HistoricalYear;
import com.example.ProjectManagement.dto.NotesDto.NotesResponse;
import com.example.ProjectManagement.service.NotesRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/project-management-service")
@RequiredArgsConstructor
public class NotesRecordController {
    @Autowired
    private NotesRecordService notesRecordService;
    // ✅ Utility method to enforce client_name check
    private void validateClientName(String clientName) {
        if (!"mapx".equalsIgnoreCase(clientName)) {
            if(!"mapdesk".equalsIgnoreCase(clientName)) {
                throw new IllegalArgumentException("Invalid client_name. Expected 'mapx' or 'mapdesk'.");
            }
        }
    }

    //GET request to get the notes details by id
    @GetMapping("/get-note-by-id/{noteId}")
    public ResponseEntity<GetNoteResponse> getNoteById(
            @PathVariable String noteId,
            @RequestHeader("client_name") String clientName
    ) {
        validateClientName(clientName);
        GetNoteResponse response = notesRecordService.getNoteById(noteId);
        if ("failure".equalsIgnoreCase(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    //GET request Get Notes by Latitude, Longitude, year_in_timeline, and Project ID
    @GetMapping("/get-note-by-lat-long-year")
    public ResponseEntity<GetNoteResponse> getNotesByLatLongYear(
            @RequestParam String projectId,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam long year,
            @RequestParam String era,
            @RequestHeader("client_name") String clientName

    ) {
        validateClientName(clientName);
        HistoricalYear yearInTimeline = new HistoricalYear(year, era);
        GetNoteResponse response= notesRecordService.getNotesByLatLongYear(projectId, latitude, longitude, yearInTimeline);
        if ("failure".equalsIgnoreCase(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }


    //Get All Notes by Project ID
    @GetMapping("/get-all-note-by-project-id/{projectId}")
    public ResponseEntity<GetNoteResponse>  getAllNotesByProjectId(
            @PathVariable String projectId,
            @RequestHeader("client_name") String clientName

    ) {
        validateClientName(clientName);
        GetNoteResponse response= notesRecordService.getAllNotesByProjectId(projectId);
        if ("failure".equalsIgnoreCase(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }



    //Get All Notes by Project ID and year
    @GetMapping("/get-all-note-by-project-id-and-year/{projectId}")
    public ResponseEntity<GetNoteResponse>  getAllNotesByProjectAndYear(
            @PathVariable String projectId,
            @RequestParam long year,
            @RequestParam String era,
            @RequestHeader("client_name") String clientName

    ) {
        validateClientName(clientName);
        HistoricalYear yearInTimeline = new HistoricalYear(year, era);
        GetNoteResponse response= notesRecordService.getAllNotesByProjectIdAndYear(projectId, yearInTimeline);
        if ("failure".equalsIgnoreCase(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }


    //To create a new notes

    @PostMapping("/create-new-note")
    public ResponseEntity<NotesResponse> createNewNote(
            @RequestBody CreateNoteRequest request,
            @RequestHeader("client_name") String clientName
    ) {
        validateClientName(clientName);
        NotesResponse response = notesRecordService.createNewNote(request);
        if ("failure".equalsIgnoreCase(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    //update API request
    @PatchMapping("/update-note/{noteId}")
    public ResponseEntity<NotesResponse> updateNote(
            @PathVariable String noteId,
            @RequestParam String email,
            @RequestBody UpdateNoteRequest request,
            @RequestHeader("client_name") String clientName
    ){
        validateClientName(clientName);

        NotesResponse response=notesRecordService.updateNote(noteId,email,request);
        if ("failure".equalsIgnoreCase(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    //Delete API request by ID and email by query
    @DeleteMapping("/delete-note/{noteId}")
    public ResponseEntity<Response> deleteNote(
            @PathVariable String noteId,
            @RequestParam String email,
            @RequestHeader("client_name") String clientName
    ){
        validateClientName(clientName);
        Response response = notesRecordService.deleteNoteById(noteId, email);
        if(response.getStatus().equalsIgnoreCase("failure")){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }

}