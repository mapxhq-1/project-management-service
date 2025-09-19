package com.example.ProjectManagement.dto.MapShapesDto;

import lombok.AllArgsConstructor;
import lombok.Data;


//Used for the GET request for notes to return the status and message with object
@Data
@AllArgsConstructor
public class GetMapShapesResponse {
    private String status;
    private String message;
    private Object mapShapes; // Will hold NoteResponseDto or null
}
