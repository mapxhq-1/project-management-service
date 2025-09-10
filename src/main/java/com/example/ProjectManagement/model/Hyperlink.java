package com.example.ProjectManagement.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;


@AllArgsConstructor
@Data
@NoArgsConstructor
@Document("hyperlink_collection")
public class Hyperlink {
    @Id
    private String id;
    private String projectId;
    private String email;
    private String hyperlinkTitle;
    private HistoricalYear yearInTimeline;
    private Double latitude;
    private Double longitude;
    private String hyperlink;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
