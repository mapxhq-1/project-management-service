package com.example.ProjectManagement.repository;

import com.example.ProjectManagement.model.Notes;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NotesRecordRepository extends MongoRepository<Notes,String> {
    List<Notes> findByProjectId(String projectId);
}

