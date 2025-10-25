package com.example.ProjectManagement.dto.MapShapesDto;


import lombok.*;


//These is entity for the successfull or failure for a given API

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapShapesResponse{
    private String status;
    private String message;
    private  String shapeId;
}
