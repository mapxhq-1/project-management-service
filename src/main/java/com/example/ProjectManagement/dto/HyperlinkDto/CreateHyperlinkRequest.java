package com.example.ProjectManagement.dto.HyperlinkDto;

import com.example.ProjectManagement.model.HistoricalYear;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateHyperlinkRequest {
    private String projectId;               // Required
    private String email;                   // Required
    private String hyperlinkTitle;                // Required
    private HistoricalYear yearInTimeline;  // Required
    private double latitude;                 // Required
    private double longitude;                // Required
    private String hyperlink;                 // Required
}
