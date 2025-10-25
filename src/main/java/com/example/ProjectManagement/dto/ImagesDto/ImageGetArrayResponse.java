package com.example.ProjectManagement.dto.ImagesDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageGetArrayResponse {
   private String  status;
    private String message;
    private Object images;
}
