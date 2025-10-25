package com.example.ProjectManagement.dto.CloneProjectDto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloneProjectResponse {
    @Id
    String projectId;
    String  status;
}
