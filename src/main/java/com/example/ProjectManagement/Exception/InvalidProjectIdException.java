package com.example.ProjectManagement.Exception;

public class InvalidProjectIdException extends RuntimeException{
    public InvalidProjectIdException(){
        super("Projec with given project Id is not found");
    }
}
