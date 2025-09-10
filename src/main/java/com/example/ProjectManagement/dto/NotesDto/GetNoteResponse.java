package com.example.ProjectManagement.dto.NotesDto;

import lombok.AllArgsConstructor;
import lombok.Data;


//Used for the GET request for notes to return the status and message with object
@Data
@AllArgsConstructor
public class GetNoteResponse {
    private String status;
    private String message;
    private Object note; // Will hold NoteResponseDto or null
}
