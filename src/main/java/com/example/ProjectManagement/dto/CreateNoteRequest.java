package com.example.ProjectManagement.dto;

import com.example.ProjectManagement.model.HistoricalYear;
import lombok.Data;

@Data
public class CreateNoteRequest {
    private String projectId;               // Required
    private String email;                   // Required
    private String noteTitle;                // Required
    private HistoricalYear yearInTimeline;  // Required
    private double latitude;                 // Required
    private double longitude;                // Required
    private String htmlText;                 // Required
}
