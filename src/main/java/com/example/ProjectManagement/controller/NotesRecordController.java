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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project-management-service")
@RequiredArgsConstructor
public class NotesRecordController {
    @Autowired
    private NotesRecordService notesRecordService;

    //GET request to get the notes details by id
    @GetMapping("/get-note-by-id/{noteId}")
    public ResponseEntity<GetNoteResponse> getNoteById(@PathVariable String noteId) {
        GetNoteResponse response = notesRecordService.getNoteById(noteId);
        if ("failure".equalsIgnoreCase(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    //GET request Get Notes by Latitude, Longitude, year_in_timeline, and Project ID
    @GetMapping("/get-note-by-lat-long-year")
    public GetNoteResponse getNotesByLatLongYear(
            @RequestParam String projectId,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam long year,
            @RequestParam String era
    ) {
        HistoricalYear yearInTimeline = new HistoricalYear(year, era);
        return notesRecordService.getNotesByLatLongYear(projectId, latitude, longitude, yearInTimeline);
    }

    //Get All Notes by Project ID and year
    @GetMapping("/get-all-note-by-project-id-and-year/{projectId}")
    public GetNoteResponse getAllNotesByProjectAndYear(
            @PathVariable String projectId,
            @RequestParam long year,
            @RequestParam String era
    ) {
        HistoricalYear yearInTimeline = new HistoricalYear(year, era);
        return notesRecordService.getAllNotesByProjectIdAndYear(projectId, yearInTimeline);
    }


    //To create a new notes

    @PostMapping("/create-new-note")
    public ResponseEntity<NotesResponse> createNewNote(@RequestBody CreateNoteRequest request) {
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
            @RequestBody UpdateNoteRequest request){
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
            @RequestParam String email){
        Response response = notesRecordService.deleteNoteById(noteId, email);
        return ResponseEntity.ok(response);
    }

}
