package com.example.ProjectManagement.controller;


import com.example.ProjectManagement.dto.CreateNoteRequest;
import com.example.ProjectManagement.dto.Response;
import com.example.ProjectManagement.model.StatusResponse;
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

    @PostMapping("/create-new-note")
    public ResponseEntity<StatusResponse> createNewNote(@RequestBody CreateNoteRequest request) {
        StatusResponse response = notesRecordService.createNewNote(request);
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
