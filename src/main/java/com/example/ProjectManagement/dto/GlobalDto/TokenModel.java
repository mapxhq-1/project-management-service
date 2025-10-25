package com.example.ProjectManagement.dto.GlobalDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenModel {
    private String token;

    public TokenModel(String token) {
        this.token=token;
    }


}

