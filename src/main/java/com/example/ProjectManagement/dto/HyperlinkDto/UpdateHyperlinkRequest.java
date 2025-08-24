package com.example.ProjectManagement.dto.HyperlinkDto;


import com.example.ProjectManagement.model.HistoricalYear;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateHyperlinkRequest {
    private String hyperlink;
     private  HistoricalYear yearInTimeline;
}
