package com.example.ProjectManagement.service;

import com.example.ProjectManagement.dto.MapShapesDto.*;
import com.example.ProjectManagement.model.HistoricalYear;
import com.example.ProjectManagement.model.MapShapes;
import com.example.ProjectManagement.model.Project;
import com.example.ProjectManagement.repository.MapShapesRepository;
import com.example.ProjectManagement.repository.ProjectRecordRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.Files; // Import for fast reading
import java.nio.file.Path;  // Import for paths
import java.time.Instant;
import java.util.*;

@Service
public class MapShapesService {

    @Autowired
    private MapShapesRepository mapsRepository;

    @Autowired
    private ProjectRecordRepository projectRepository;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/map_shapes_content/";


    public boolean checkEra(String  era){
        // ✅ Using switch for era check
        return switch (era) {
            case "BCE", "CE" -> true;
            // valid, do nothing
            default -> false;
        };
    }

    //GET Method to fetch the map shapes details by shapeId
    public GetMapShapesResponse getMapShapeById(String shapeId) {
        // Validate ID
        if (shapeId == null || shapeId.isEmpty() || !ObjectId.isValid(shapeId)) {
            return new GetMapShapesResponse("failure", "Invalid or missing shape ID", null);
        }

        // Fetch from DB
        MapShapes mapShapes = mapsRepository.findById(shapeId).orElse(null);
        if (mapShapes == null) {
            return new GetMapShapesResponse("failure", "mapShapes not found", null);
        }
        String geojsonContent = "{}";
        try {
            // FAST READ: Read file as String directly
            String filePath = UPLOAD_DIR + mapShapes.getFileId() + ".json";
            geojsonContent = Files.readString(Path.of(filePath));
        } catch (Exception e) {
            return new GetMapShapesResponse("failure", "Failed to read geojson content from disk", null);
        }

        MapShapesResponseDto dto = new MapShapesResponseDto(
                mapShapes.getId(),
                mapShapes.getProjectId(),
                mapShapes.getYearInTimeline(),
                mapShapes.getEmail(),
                mapShapes.getCreatedAt(),
                mapShapes.getUpdatedAt(),
                geojsonContent // Pass raw string
        );

        return new GetMapShapesResponse("success", null, dto);
    }


    //Get All map shapes by Project ID and year
    public GetMapShapesResponse getAllMapShapesByProjectAndYear(
            String projectId,
            HistoricalYear yearInTimeline
    ) {
        if (projectId == null || projectId.isEmpty()) {
            return new GetMapShapesResponse("failure", "Invalid or missing projectId", null);
        }
        if (yearInTimeline == null ||
                yearInTimeline.getEra() == null || yearInTimeline.getEra().isEmpty()) {
            return new GetMapShapesResponse("failure", "Invalid or missing yearInTimeline", null);
        }
        if(!checkEra(yearInTimeline.getEra())){
            return new GetMapShapesResponse("failure", "Give the correct era BCE or CE", null);
        }
        // Fetch from DB
        List<MapShapes> mapShapes = mapsRepository.findByProjectIdAndYearInTimeline(
                projectId,yearInTimeline
        );

        if (mapShapes.isEmpty()) {
            return new GetMapShapesResponse("failure", "No map shapes found",new ArrayList<>());
        }

        List<MapShapesResponseDto> mapShapesDtos=new ArrayList<>();
        for(MapShapes mapShape:mapShapes){
            // Read HTML content
            String geojsonContent = "{}";
            try {
                // FAST READ: Read file as String directly
                String filePath = UPLOAD_DIR + mapShape.getFileId() + ".json";
                geojsonContent = Files.readString(Path.of(filePath));
            } catch (Exception e) {
                return new GetMapShapesResponse("failure", "Failed to read geojson content from disk", null);
            }
            MapShapesResponseDto dto=new MapShapesResponseDto(
                    mapShape.getId(),
                    mapShape.getProjectId(),
                    mapShape.getYearInTimeline(),
                    mapShape.getEmail(),
                    mapShape.getCreatedAt(),
                    mapShape.getUpdatedAt(),
                    geojsonContent // Pass raw string
                    );
            mapShapesDtos.add(dto);

        }


        return new GetMapShapesResponse("success", null, mapShapesDtos);
    }






    //create a map shapes service

    public MapShapesResponse createNewMapShapes(CreateMapShapeRequest request) {

        // Validate required fields
        if (request.getProjectId() == null || request.getProjectId().trim().isEmpty()) {
            return new MapShapesResponse("failure", "Project ID is required", null);
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return new MapShapesResponse("failure", "Email is required", null);
        }
        if(!checkEra(request.getYearInTimeline().getEra())){
            return new MapShapesResponse("failure", "Era should CE or BCE", null);

        }
        if (request.getGeojson() == null) {
            return new MapShapesResponse("failure", "Geojson Content is required", null);
        }

        // Check if project exists
        Optional<Project> project = projectRepository.findById(request.getProjectId());
        if (project.isEmpty()) {
            return new MapShapesResponse("failure", "Project not found", null);
        }

        try {
            // Create HTML file

            String uuid = UUID.randomUUID().toString();

            // Save JSON file
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(UPLOAD_DIR + uuid + ".json");
            FileWriter writer = new FileWriter(file);
            ObjectMapper mapper = new ObjectMapper();
            writer.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request.getGeojson()));
            writer.close();


            // Save to DB
            MapShapes map = new MapShapes();
            map.setProjectId(request.getProjectId());
            map.setEmail(request.getEmail()); // ✅ use header email
            map.setYearInTimeline(request.getYearInTimeline());
            map.setFileId(uuid);
            map.setCreatedAt(Instant.now());
            map.setUpdatedAt(Instant.now());

            MapShapes savedmap = mapsRepository.save(map);

            return new MapShapesResponse("success", null, savedmap.getId());

        } catch (Exception e) {
            return new MapShapesResponse("failure", "Error while creating map shapes: " + e.getMessage(), null);
        }
    }



    //logic for update the map shapes
    public MapShapesResponse updateMapShapes(String shapeId, String email, UpdateMapShapesRequest updateRequest) {
        // Validate noteId
        if (shapeId == null || shapeId.isEmpty() || !ObjectId.isValid(shapeId)) {
            return new MapShapesResponse("failure", "Invalid or missing shape ID", null);
        }

        // Validate email
        if (email == null || email.isEmpty()) {
            return new MapShapesResponse("failure", "Email is required", null);
        }

        // Validate htmlText
        if (updateRequest.getGeojson() == null) {
            return new MapShapesResponse("failure", "geojson content must not be null or empty", null);
        }

        // Fetch note
        MapShapes mapShapes = mapsRepository.findById(shapeId).orElse(null);
        if (mapShapes == null) {
            return new MapShapesResponse("failure", "Map shapes not found", null);
        }

        // Authorization check
        if (!email.equalsIgnoreCase(mapShapes.getEmail())) {
            return new MapShapesResponse("failure", "Unauthorized: email does not match map shapes owner", null);
        }

        // Update HTML file
        // Update HTML file
        try {
            String jsonFileId = mapShapes.getFileId();
            File file = new File(UPLOAD_DIR + jsonFileId + ".json");
            FileWriter writer = new FileWriter(file);
            ObjectMapper mapper = new ObjectMapper();
            writer.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(updateRequest.getGeojson()));
            writer.close();
        } catch (Exception e) {
            return new MapShapesResponse("failure", "Failed to update  geojson content", null);
        }

        // Update MongoDB record
        try {
            mapShapes.setYearInTimeline(updateRequest.getYearInTimeline());
            mapShapes.setUpdatedAt(Instant.now());
            mapsRepository.save(mapShapes);
        } catch (Exception e) {
            return new MapShapesResponse("failure", "Failed to update map shapes record", null);
        }

        return new MapShapesResponse("success", null, shapeId);
    }






        //logic for delete
    public Response deleteMapShapeById(String shapeId, String email) {
        // Validate noteId
        if (shapeId == null || shapeId.isEmpty() || !ObjectId.isValid(shapeId)) {
            return new Response("failure", "Invalid or missing shape ID");
        }

        // Validate email
        if (email == null || email.isEmpty()) {
            return new Response("failure", "Email is required");
        }

        // Fetch note
        MapShapes mapShapes = mapsRepository.findById(shapeId).orElse(null);
        if (mapShapes == null) {
            return new Response("failure", "mapShapes not found");
        }

        // Check authorization
        if (!email.equalsIgnoreCase(mapShapes.getEmail())) {
            return new Response("failure", "Unauthorized: email does not match mapshapes owner");
        }

        // Delete HTML file (best effort)
        try {
            String jsonFileId = mapShapes.getFileId();
            if (jsonFileId != null && !jsonFileId.isEmpty()) {
                // Store outside the JAR so deletion works
                File file=new File(UPLOAD_DIR+jsonFileId+".json");
                if(file.exists()){
                    if(!file.delete()){
                        System.err.println("warning: Failed to delete file: "+file.getAbsolutePath());
                    }
                    else{
                        System.out.println("Note: File not found during delete operation: " + file.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error deleting HTML file: " + e.getMessage());
        }

        // Delete MongoDB record
        try {
            mapsRepository.deleteById(shapeId);
            return new Response("success", null);
        } catch (Exception e) {
            return new Response("failure", "Failed to delete map shapes record");
        }
    }


}
