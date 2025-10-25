package com.example.ProjectManagement.repository;


import com.example.ProjectManagement.model.HistoricalYear;
import com.example.ProjectManagement.model.MapShapes;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MapShapesRepository extends MongoRepository<MapShapes,String> {


    // Fetch from DB

    List<MapShapes> findByProjectId(
            String projectId
    );

    List<MapShapes> findByProjectIdAndYearInTimeline(
             String projectId,
             HistoricalYear yearInTimeline
    );






    }