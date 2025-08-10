package com.example.ProjectManagement.repository;

import com.example.ProjectManagement.model.Images;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ImagesRecordRepository extends MongoRepository<Images,String> {
}

