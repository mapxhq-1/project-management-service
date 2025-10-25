package com.example.ProjectManagement.dto.projectDto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatusResponse{
    private String status;
    private String message;
    private  String ProjectId;
}
