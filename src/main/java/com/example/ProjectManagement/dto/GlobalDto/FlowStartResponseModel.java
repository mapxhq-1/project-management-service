package com.example.ProjectManagement.dto.GlobalDto;
import cloud.pangeacyber.pangea.authn.results.ClientTokenCheckResult;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlowStartResponseModel {
    private String status;
    private String message;
    private ClientTokenCheckResult clientTokenCheckResult;
}
