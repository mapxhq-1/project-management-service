package com.example.ProjectManagement.repository;

import com.example.ProjectManagement.model.Notes;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NotesRecordRepository extends MongoRepository<Notes,String> {
}

