package com.example.ProjectManagement.dto.NotesDto;


import lombok.*;


//These is entity for the successfull or failure for a given API
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotesResponse{
    private String status;
    private String message;
    private  String noteId;
}
