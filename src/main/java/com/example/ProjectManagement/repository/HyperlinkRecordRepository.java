package com.example.ProjectManagement.repository;


import com.example.ProjectManagement.model.Hyperlink;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HyperlinkRecordRepository extends MongoRepository<Hyperlink,String> {


}
