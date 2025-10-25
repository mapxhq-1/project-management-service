package com.example.ProjectManagement.dto.MapShapesDto;


import com.example.ProjectManagement.model.HistoricalYear;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.TreeMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMapShapesRequest {
    private TreeMap<String,Object> geojson;                // Required
    private HistoricalYear yearInTimeline;  // Required
}
