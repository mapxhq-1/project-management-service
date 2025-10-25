package com.example.ProjectManagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notes_collection")
public class Notes {

    @Id
    private String id;  // MongoDB ObjectId as String

    private String projectId;
    private String email;
    private String noteTitle;
    private HistoricalYear yearInTimeline;
    private Double latitude;
    private Double longitude;
    private String htmlFileId;
    private String  backgroundColor;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
