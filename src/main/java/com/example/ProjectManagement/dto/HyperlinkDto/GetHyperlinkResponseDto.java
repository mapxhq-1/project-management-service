package com.example.ProjectManagement.dto.HyperlinkDto;

import com.example.ProjectManagement.model.HistoricalYear;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class GetHyperlinkResponseDto {
    private String hyperlinkId;
    private String projectId;
    private String hyperlinkTitle;
    private double latitude;
    private double longitude;
    private HistoricalYear yearInTimeline;
    private String hyperlink;
    private Instant createdAt;
    private Instant updatedAt;
}
