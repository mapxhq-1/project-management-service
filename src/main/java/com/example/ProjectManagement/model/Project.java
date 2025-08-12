package com.example.ProjectManagement.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "project_records")
public class Project {

    @Id
    private String id;  // MongoDB ObjectId

    private String ownerEmail; // required

    private List<String> accessorList; // default empty list

    private String projectName; // required

    private Map<String, Object> projectConfig; // default empty map

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Document(collection = "notes_collection")
    public static class Notes {

        @Id
        private String id;  // MongoDB ObjectId as String

        private String projectId;
        private String email;
        private String noteTitle;
        private HistoricalYear yearInTimeline;
        private Double latitude;
        private Double longitude;
        private String htmlFileId;

        @CreatedDate
        private Instant createdAt;

        @LastModifiedDate
        private Instant updatedAt;
    }
}
