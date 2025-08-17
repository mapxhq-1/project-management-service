package com.example.ProjectManagement.repository;

import com.example.ProjectManagement.model.Images;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ImagesRecordRepository extends MongoRepository<Images,String> {
    List<Images> findByProjectIdAndLatitudeAndLongitude(
            String projectId,
            double latitude,
            double longitude
    );
    List<Images> findByProjectId(
            String projectId
    );
}

