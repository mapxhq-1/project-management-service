package com.example.ProjectManagement.service;


import com.example.ProjectManagement.dto.ImagesDto.ImageGetArrayResponse;
import com.example.ProjectManagement.dto.ImagesDto.ImageGetResponse;
import com.example.ProjectManagement.dto.ImagesDto.ImageUploadResponse;
import com.example.ProjectManagement.dto.NotesDto.GetNoteResponse;
import com.example.ProjectManagement.dto.NotesDto.Response;
import com.example.ProjectManagement.model.Images;
import com.example.ProjectManagement.model.HistoricalYear;

import java.nio.file.Files;
import java.time.Instant;

import com.example.ProjectManagement.model.Project;
import com.example.ProjectManagement.repository.ImagesRecordRepository;
import com.example.ProjectManagement.repository.ProjectRecordRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImagesRecordService {

    @Autowired
    private ImagesRecordRepository imagesRepository;
    @Autowired
    private ProjectRecordRepository projectRepository;
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/image_content/";


    //To check the era

    public boolean checkEra(String  era){
        // ✅ Using switch for era check
        switch (era) {
            case "BCE": return true;
            case "CE":return true;
            // valid, do nothing
            default:
                break;
        }
        return false;
    }

    //GET API API 1: Get Image by ID
    public ImageGetResponse getById(String imageId){
          if(imageId==null || imageId.isEmpty()|| !ObjectId.isValid(imageId)){
              return new ImageGetResponse("failure", "Missing or invalid imageId", null);
          }
          Optional<Images> imageOpt=imagesRepository.findById(imageId);
          if(imageOpt.isEmpty()){
              return  new ImageGetResponse("failure","Image not found",null);
          }
          Images image=imageOpt.get();

            return new ImageGetResponse("success", null, image);
    }

    //GET API 2:Get All Images by Latitude and Longitude
    public ImageGetArrayResponse getImageByLatLong(String projectId,double latitude,double longitude){
        if (projectId == null || projectId.isEmpty() || !ObjectId.isValid(projectId)) {
            return new ImageGetArrayResponse("failure", "Missing or invalid projectId", null);
        }
        if (latitude < -90 || latitude > 90) {
            return new ImageGetArrayResponse("failure", "Invalid latitude", null);
        }
        if (longitude < -180 || longitude > 180) {
            return new ImageGetArrayResponse("failure", "Invalid longitude", null);
        }
        List<Images> image=imagesRepository.findByProjectIdAndLatitudeAndLongitude(projectId,latitude,longitude);
        if (image.isEmpty()) {
            return new ImageGetArrayResponse("failure", "No images found", null);
        }
        return new ImageGetArrayResponse("success",null,image);
    }



    // GET API 3:Get All Images by Project ID
    public ImageGetArrayResponse getImagesByProjectId(String projectId)
    {
        if(projectId==null || projectId.isEmpty() ||!ObjectId.isValid(projectId)){
            return new ImageGetArrayResponse("failure","Invalid or missing projectId",null);
        }
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isEmpty()) {
            return new ImageGetArrayResponse("failure", "Project not found", null);
        }
        List<Images> image=imagesRepository.findByProjectId(projectId);
        if(image.isEmpty()){
            return new ImageGetArrayResponse("failure", "No image found",null);
        }
        return new ImageGetArrayResponse("success",null,image);
    }

    //GET API 4: Fetch Image Content by File Name
   public ResponseEntity<byte []> getImageByFileName(String fileName) throws IOException {
        File file=new File(UPLOAD_DIR+fileName);
       // Check if file exists
       if (!file.exists()) {
           return ResponseEntity.notFound().build(); // 404 Not Found
       }

       // Read file bytes
       byte[] content = Files.readAllBytes(file.toPath());

       // Detect content type
       String contentType = Files.probeContentType(file.toPath());

       return ResponseEntity.ok()
               .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
               .body(content);
   }



    //POST API  Implement Upload New Image Endpoint (multipart form-data)
    public ImageUploadResponse uploadImage(String projectId, String email, String latitudeStr, String longitudeStr,
                                           MultipartFile imageFile, String caption, String yearStr, String era) {

        // 1️⃣ Validate inputs
        if (projectId == null || projectId.isEmpty() || !ObjectId.isValid(projectId)) {
            return new ImageUploadResponse("failure", "Missing or invalid project_id", null);
        }
        if (email == null || email.isEmpty()) {
            return new ImageUploadResponse("failure", "Email is required", null);
        }
        if(!checkEra(era)){
            return new ImageUploadResponse("failure", "Give the correct era BCE or CE", null);
        }
        double latitude;
        double longitude;
        try {
            latitude=Double.parseDouble(latitudeStr);
            longitude=Double.parseDouble(longitudeStr);
        }catch (NumberFormatException e) {
            return new ImageUploadResponse("failure", "Invalid latitude/longitude format", null);
        }
        if (latitude < -90 || latitude > 90) {
            return new ImageUploadResponse("failure", "Invalid latitude", null);
        }
        if (longitude < -180 || longitude > 180) {
            return new ImageUploadResponse("failure", "Invalid longitude", null);
        }

        if (imageFile == null || imageFile.isEmpty()) {
            return new ImageUploadResponse("failure", "Empty or invalid image file", null);
        }

        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isEmpty()) {
            return new ImageUploadResponse("failure", "Project not found", null);
        }

        // 2️⃣ Generate fileId and extension
        String fileId = UUID.randomUUID().toString();
        String extension = Optional.ofNullable(imageFile.getOriginalFilename())
                .filter(f -> f.contains("."))
                .map(f -> f.substring(imageFile.getOriginalFilename().lastIndexOf('.') + 1))
                .orElse("");

        // 3️⃣ Save file to /resources/image_content/
        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File destFile = new File(UPLOAD_DIR + fileId + "." + extension);
            imageFile.transferTo(destFile);
        } catch (IOException e) {
            return new ImageUploadResponse("failure", "Failed to save image file", null);
        }

        // 4️⃣ Save metadata in MongoDB
        Images img = new Images();
        img.setProjectId(projectId);
        img.setEmail(email);
        img.setLatitude(latitude);
        img.setLongitude(longitude);
        img.setImageFileId(fileId);
        img.setCaption(caption);
        img.setYearInTimeline(new HistoricalYear(Long.parseLong(yearStr), era));
        img.setFormat(extension);
        img.setCreatedAt(Instant.now());
        img.setUpdatedAt(Instant.now());

        try {
            imagesRepository.save(img);
            return new ImageUploadResponse("success", null, img.getId());
        } catch (Exception e) {
            return new ImageUploadResponse("failure", "Failed to save image metadata", null);
        }
    }

    //UPDATE API :Update Image content
    public ImageUploadResponse updateImage(
            String imageId,String email,
            MultipartFile imageFile, String caption, String year, String era)
    {
        if (imageId == null || imageId.isEmpty() || !ObjectId.isValid(imageId)) {
            return new ImageUploadResponse("failure", "Missing or invalid project_id", null);
        }
        if (email == null || email.isEmpty()) {
            return new ImageUploadResponse("failure", "Email is required", null);
        }

        // 3️⃣ Validate yearInTimeline
        if (year == null || era == null || year.isEmpty() || era.isEmpty()) {
            return new ImageUploadResponse("failure", "Invalid or missing yearInTimeline", null);
        }
        // 4️⃣ Ensure at least one updatable field
        if ((imageFile == null || imageFile.isEmpty()) && (caption == null || caption.isEmpty())) {
            return new ImageUploadResponse("failure", "At least image file or caption must be provided", null);
        }
        if(!checkEra(era)){
            return new ImageUploadResponse("failure", "Give the correct era BCE or CE", null);
        }
        Optional<Images> imageOpt = imagesRepository.findById(imageId);

        if (imageOpt.isEmpty()) {
            return new ImageUploadResponse("failure", "ImageID not found", null);
        }

        Images image = imageOpt.get();
        if (!email.equalsIgnoreCase(image.getEmail())) {
            return new ImageUploadResponse("failure", "Email mismatch: unauthorized update attempt", null);
        }
        // 7️⃣ Update file if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String oldExtension = image.getFormat();
            String fileId = image.getImageFileId();

            // Delete old file
            File oldFile = new File(UPLOAD_DIR + fileId + "." + oldExtension);
            if (oldFile.exists()) {
                boolean deleted = oldFile.delete();
                if (!deleted) {
                    return new ImageUploadResponse("failure", "Failed to delete old image file", null);
                }
            }

            // Save new file
            String newExtension = Optional.ofNullable(imageFile.getOriginalFilename())
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(imageFile.getOriginalFilename().lastIndexOf('.') + 1))
                    .orElse("");

            try {
                File dir = new File(UPLOAD_DIR);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File destFile = new File(UPLOAD_DIR + fileId + "." + newExtension);
                imageFile.transferTo(destFile);

                // Update format in DB
                image.setFormat(newExtension);

            } catch (IOException e) {
                return new ImageUploadResponse("failure", "Failed to overwrite existing image file", null);
            }
        }

        // 8️⃣ Update caption if provided
        if (caption != null && !caption.isEmpty()) {
            image.setCaption(caption);
        }

        // 9️⃣ Update yearInTimeline
        HistoricalYear histYear = new HistoricalYear();
        histYear.setYear(Long.parseLong(year));
        histYear.setEra(era);
        image.setYearInTimeline(histYear);

        // 1️⃣0️⃣ Update updatedAt
        image.setUpdatedAt(Instant.now());
        // 1️⃣1️⃣ Save updated metadata
        imagesRepository.save(image);

        // 1️⃣2️⃣ Return success
        return new ImageUploadResponse("success", null, imageId);
    }







    //DELETE API API: Delete Image by ID API with Email Validation
    public Response deleteImageById(String imageId, String email) {

        // 1️⃣ Validate imageId
        if (imageId == null || imageId.isEmpty() || !ObjectId.isValid(imageId)) {
            return new Response("failure", "Missing or invalid imageId");
        }

        // 2️⃣ Validate email
        if (email == null || email.isEmpty()) {
            return new Response("failure", "Email is required");
        }

        // 3️⃣ Fetch image metadata
        Images image = imagesRepository.findById(imageId).orElse(null);
        if (image == null) {
            return new Response("failure", "Image metadata not found");
        }

        // 4️⃣ Email ownership check
        if (!email.equalsIgnoreCase(image.getEmail())) {
            return new Response("failure", "Email mismatch: unauthorized delete attempt");
        }

        // 5️⃣ Delete image file (best effort)
        try {
            String imageFileId = image.getImageFileId();
            String format = image.getFormat();

            if (imageFileId != null && !imageFileId.isEmpty() && format != null && !format.isEmpty()) {
                File file = new File(UPLOAD_DIR + imageFileId + "." + format);
                if (file.exists()) {
                    boolean deleted = file.delete();
                    if (!deleted) {
                        return new Response("failure", "Failed to delete image file from disk");
                    }
                }
            }
        } catch (Exception e) {
            return new Response("failure", "Failed to delete image file from disk");
        }

        // 6️⃣ Delete metadata from MongoDB
        try {
            imagesRepository.deleteById(imageId);
            return new Response("success", null);
        } catch (Exception e) {
            return new Response("failure", "Failed to delete image metadata");
        }
    }
}
