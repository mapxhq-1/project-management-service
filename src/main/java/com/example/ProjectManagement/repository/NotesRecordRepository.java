package com.example.ProjectManagement.repository;

import com.example.ProjectManagement.model.HistoricalYear;
import com.example.ProjectManagement.model.Notes;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface NotesRecordRepository extends MongoRepository<Notes,String> {
    List<Notes> findByProjectId(
      String  projectId
    );
    List<Notes> findByProjectIdAndLatitudeAndLongitudeAndYearInTimeline(
            String projectId,
            double latitude,
            double longitude,
            HistoricalYear yearInTimeline
    );

    List<Notes> findByProjectIdAndYearInTimeline(
            String projectId,
            HistoricalYear yearInTimeline
    );

}

