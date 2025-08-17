package com.example.ProjectManagement.dto.NotesDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;


//These Entity is the response for the get-by-id for notes GET Request
@Data
@AllArgsConstructor
public class NoteResponseDto{
    private String noteId;
    private String projectId;
    private double latitude;
    private double longitude;
    private Instant createdAt;
    private Instant updatedAt;
    private String noteContent; // full HTML content
}
