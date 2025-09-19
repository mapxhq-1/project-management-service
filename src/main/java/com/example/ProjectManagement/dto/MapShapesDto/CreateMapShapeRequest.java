package com.example.ProjectManagement.dto.MapShapesDto;

import com.example.ProjectManagement.model.HistoricalYear;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.TreeMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateMapShapeRequest {
    private String projectId;               // Required
    private String email;
    private HistoricalYear yearInTimeline;  // Required
    private TreeMap<String, Object> geojson; // Accepts any JSON structur

}
