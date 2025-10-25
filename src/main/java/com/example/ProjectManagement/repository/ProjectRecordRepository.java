package com.example.ProjectManagement.repository;

import com.example.ProjectManagement.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProjectRecordRepository extends MongoRepository<Project,String> {
}

