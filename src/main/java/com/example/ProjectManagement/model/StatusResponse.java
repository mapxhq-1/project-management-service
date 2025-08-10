package com.example.ProjectManagement.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatusResponse{
    private String status;
    private String message;
    private  String file_id;
}
