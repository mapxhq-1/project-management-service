package com.example.ProjectManagement.dto.GlobalDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowStartResponseModel {
    private String status;   // "success" or "failure"
    private String message;  // error or info message
}
