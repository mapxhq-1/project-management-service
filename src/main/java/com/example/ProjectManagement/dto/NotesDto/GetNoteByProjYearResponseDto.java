package com.example.ProjectManagement.dto.NotesDto;

import com.example.ProjectManagement.model.HistoricalYear;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetNoteByProjYearResponseDto {

    private String noteId;
    private String projectId;
    private  String  noteTitle;
    private double latitude;
    private double longitude;
    private String  backgroundColor;
    private HistoricalYear yearInTimeline;
    private String noteContent;
    private Instant createdAt;
    private Instant updatedAt;

}
