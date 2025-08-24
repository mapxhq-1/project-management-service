package com.example.ProjectManagement.controller;


import com.example.ProjectManagement.dto.ImagesDto.ImageGetArrayResponse;
import com.example.ProjectManagement.dto.ImagesDto.ImageGetResponse;
import com.example.ProjectManagement.dto.ImagesDto.ImageUploadResponse;
import com.example.ProjectManagement.dto.NotesDto.Response;
import com.example.ProjectManagement.model.HistoricalYear;
import com.example.ProjectManagement.service.ImagesRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/project-management-service")
public class ImagesRecordController {

     @Autowired
    private ImagesRecordService imageService;



     //GET API API 1: Get Image by ID
    @GetMapping("/get-image-by-id/{imageId}")
    public ResponseEntity<ImageGetResponse> getById(@PathVariable String imageId){
        ImageGetResponse response=imageService.getById(imageId);
        if(response.getStatus().equalsIgnoreCase("failure")){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }

    //GET API 2:Get All Images by Latitude and Longitude
    @GetMapping("/get-all-image-by-lat-long")
    public ResponseEntity<ImageGetArrayResponse> getImageByLatLong(
            @RequestParam("projectId") String  projectId,
            @RequestParam("latitude") double  latitude,
            @RequestParam("longitude") double longitude
    )
    {
        ImageGetArrayResponse response = imageService.getImageByLatLong(projectId, latitude, longitude);
        if(response.getStatus().equalsIgnoreCase("failure")){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }


    //GET API 3:Get All Images by Latitude and Longitude and projectID and yearInTimeline
    @GetMapping("/get-all-image-by-projectId-lat-long-year-era")
    public ResponseEntity<ImageGetArrayResponse> getImageByProjectIdLatLongAndYearInTimeline(
            @RequestParam("projectId") String  projectId,
            @RequestParam("latitude") double  latitude,
            @RequestParam("longitude") double longitude,
            @RequestParam("year") long year,
            @RequestParam("era")  String era
    )
    {
        HistoricalYear yearInTimeline=new HistoricalYear(year,era);
        ImageGetArrayResponse response = imageService.getImageByProjectIdLatLongAndYearInTimeline(projectId, latitude, longitude,yearInTimeline);
        if(response.getStatus().equalsIgnoreCase("failure")){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }






    //GET API 4:Get All Images by Project ID
    @GetMapping("/get-all-image-by-project-id")
    public ResponseEntity<ImageGetArrayResponse> getImageByProjectId(
            @RequestParam String projectId
    ){
        ImageGetArrayResponse response=imageService.getImagesByProjectId(projectId);
        if(response.getStatus().equalsIgnoreCase("failure")){
             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                     .body(response);   //return 400 bad request
        }
        return ResponseEntity.ok(response); //returns 200 ok request
    }


    //Get API 5:Get All Images By project Id and year and era
    @GetMapping("/get-all-images-by-project-id-year-in-timeline")
    public  ResponseEntity<ImageGetArrayResponse>  getImageByProjectIdYearInTimeline(
            @RequestParam String  projectId,
            @RequestParam  long year,
            @RequestParam String era
    ){
        HistoricalYear yearInTimeline = new HistoricalYear(year, era);
        ImageGetArrayResponse response=imageService.getImageByProjectIdYearInTimeline(projectId,yearInTimeline);
        if(response.getStatus().equalsIgnoreCase("failure")){
             return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }


     //GET API 6:fetch Image Content by File Name
    @GetMapping("/fetch-image-content/{fileName}")
    public ResponseEntity<byte[]> getImageByFileName(
            @PathVariable("fileName") String fileName
    ) throws IOException
    {
        return imageService.getImageByFileName(fileName);
    }


     //POST API  Implement Upload New Image Endpoint (multipart form-data)
    @PostMapping("/upload-new-image")
    public ImageUploadResponse uploadImage(
            @RequestParam("projectId") String projectId,
            @RequestParam("email") String email,
            @RequestParam("latitude") String latitude,
            @RequestParam("longitude") String longitude,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam("caption") String caption,
            @RequestParam("year") String year,
            @RequestParam("era") String era
    ) {
        return imageService.uploadImage(projectId, email, latitude, longitude, imageFile, caption, year, era);
    }

    //UPDATE API :Update Image content
    @PutMapping("/update-image-by-id/{imageId}")
    public ImageUploadResponse updateImage(
             @PathVariable("imageId") String imageId,
             @RequestParam("email") String email,
             @RequestParam("imageFile") MultipartFile imageFile,
             @RequestParam("caption") String caption,
             @RequestParam("year") String year,
             @RequestParam("era") String era
    ){
        return imageService.updateImage(imageId,email,imageFile,caption,year,era);
    }

    //DELETE API API: Delete Image by ID API with Email Validation
    @DeleteMapping("/delete-image-by-id/{imageId}")
    public Response deleteImageById(
            @PathVariable String imageId,
            @RequestParam String email
    ) {
        return imageService.deleteImageById(imageId, email);
    }
}
