package com.example.ProjectManagement.dto.HyperlinkDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetHyperlinkResponse {
    private String status;
    private String message;
    private Object hyperlink; // Will hold HyperlinkResponseDto or null
}
