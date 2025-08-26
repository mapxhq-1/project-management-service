package com.example.ProjectManagement.repository;


import com.example.ProjectManagement.model.HistoricalYear;
import com.example.ProjectManagement.model.Hyperlink;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HyperlinkRecordRepository extends MongoRepository<Hyperlink,String> {

    List<Hyperlink> findByProjectIdAndLatitudeAndLongitudeAndYearInTimeline(
             String projectId,
             double latitude,
             double longitude,
             HistoricalYear yearInTimeline
    );

    List<Hyperlink> findByProjectIdAndYearInTimeline(
        String projectId,
        HistoricalYear yearInTimeline
    );
}
