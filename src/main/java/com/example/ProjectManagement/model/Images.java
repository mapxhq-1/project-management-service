package com.example.ProjectManagement.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection ="images_collection")
public class Images {

    @Id
    private String id;

    private String projectId;        // Project ID
    private String email;            // Owner's email
    private double latitude;         // Latitude
    private double longitude;        // Longitude
    private String imageFileId;      // UUID for image file
    private String caption;          // Image caption
    private HistoricalYear yearInTimeline; // Year in timeline (nested object)
    private String format;           // Image format (png, jpg, etc.)
    private Instant createdAt;       // Created timestamp
    private Instant updatedAt;       // Updated timestamp
}

