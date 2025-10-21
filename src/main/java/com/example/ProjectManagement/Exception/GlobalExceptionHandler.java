package com.example.ProjectManagement.Exception;

import com.example.ProjectManagement.Exception.InvalidProjectIdException;
import com.example.ProjectManagement.dto.GlobalDto.FlowStartResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {


    // Handle custom InvalidProjectIdException

    @ExceptionHandler(InvalidProjectIdException.class)

    public ResponseEntity<String> handleInvalidProjectId(InvalidProjectIdException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // Handle Missing request headers (email, client_name, etc.)

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<FlowStartResponseModel> handleMissingHeader(MissingRequestHeaderException ex){
         FlowStartResponseModel response=new FlowStartResponseModel();
         response.setStatus("failure");
         response.setMessage(ex.getMessage());
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<FlowStartResponseModel> handleMissingHeader(IllegalArgumentException ex){
        FlowStartResponseModel response=new FlowStartResponseModel();
        response.setStatus("failure");
        response.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
