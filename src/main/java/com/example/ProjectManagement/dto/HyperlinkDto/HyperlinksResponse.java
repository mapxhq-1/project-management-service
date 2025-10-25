package com.example.ProjectManagement.dto.HyperlinkDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HyperlinksResponse {
    private String status;
    private String message;
    private  String hyperlinkId;
}
