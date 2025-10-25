package com.example.ProjectManagement.dto.NotesDto;

import com.example.ProjectManagement.model.HistoricalYear;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class GetNoteResponseDto {
    private String noteId;
    private String projectId;
    private  String  noteTitle;
    private double latitude;
    private double longitude;
    private HistoricalYear yearInTimeline;
    private String htmlFileId;
    private Instant createdAt;
    private Instant updatedAt;
}
