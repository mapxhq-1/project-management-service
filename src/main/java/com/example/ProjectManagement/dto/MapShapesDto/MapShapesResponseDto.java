package com.example.ProjectManagement.dto.MapShapesDto;

import com.example.ProjectManagement.model.HistoricalYear;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;
//import java.util.TreeMap;


//These Entity is the response for the get-by-id for notes GET Request
    @Data
    @AllArgsConstructor
    public class MapShapesResponseDto {
        private String shapeId;
        private String projectId;
    private HistoricalYear yearInTimeline;
    private  String  email;
        private Instant createdAt;
        private Instant updatedAt;

    // OPTIMIZATION: Use String + @JsonRawValue
        @JsonRawValue
        private  String geojson;
    }
