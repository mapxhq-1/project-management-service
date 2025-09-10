package com.example.ProjectManagement.dto.projectDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteProjectResponse {
    private String status;  // "success" or "failure"
    private String message; // null or reason for failure

    public static DeleteProjectResponse success() {
        return new DeleteProjectResponse("success", null);
    }

    public static DeleteProjectResponse failure(String message) {
        return new DeleteProjectResponse("failure", message);
    }
}
