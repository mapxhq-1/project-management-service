package com.example.ProjectManagement.dto.HyperlinkDto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class HyperlinkResponseDto {
    private String hyperlinkId;
    private String projectId;
    private double latitude;
    private double longitude;
    private Instant createdAt;
    private Instant updatedAt;
    private String hyperlink; //hyperlink
}
