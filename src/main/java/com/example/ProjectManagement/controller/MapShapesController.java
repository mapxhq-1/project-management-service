package com.example.ProjectManagement.controller;

import com.example.ProjectManagement.dto.MapShapesDto.*;
import com.example.ProjectManagement.model.HistoricalYear;
import com.example.ProjectManagement.service.MapShapesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project-management-service")
@RequiredArgsConstructor
public class MapShapesController {
    @Autowired
    private MapShapesService mapService;

    private void validateClientName(String clientName) {
        if (!"mapx".equalsIgnoreCase(clientName)) {
            if(!"mapdesk".equalsIgnoreCase(clientName)) {
                throw new IllegalArgumentException("Invalid client_name. Expected 'mapx' or 'mapdesk'.");
            }
        }
    }
//
    //GET request to get the map shapes details by id
    @GetMapping("/get-mapShape-by-id/{shapeId}")
    public ResponseEntity<GetMapShapesResponse> getMapShapeById(
            @PathVariable String shapeId,
            @RequestHeader("client_name") String clientName
    ) {
        validateClientName(clientName);
        GetMapShapesResponse response = mapService.getMapShapeById(shapeId);
        if ("failure".equalsIgnoreCase(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }


    //Get All Map Shapes by Project ID and year
    @GetMapping("/get-all-map-shapes-by-project-id-and-year/{projectId}")
    public ResponseEntity<GetMapShapesResponse>  getAllMapShapesByProjectAndYear(
            @PathVariable String projectId,
            @RequestParam long year,
            @RequestParam String era,
            @RequestHeader("client_name") String clientName

    ) {
        validateClientName(clientName);
        HistoricalYear yearInTimeline = new HistoricalYear(year, era);
        GetMapShapesResponse response= mapService.getAllMapShapesByProjectAndYear(projectId, yearInTimeline);
        if ("failure".equalsIgnoreCase(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }



    //To create a new map shapes

    @PostMapping("/create-new-mapShape")
    public ResponseEntity<MapShapesResponse> createNewNote(
            @RequestBody CreateMapShapeRequest request,
            @RequestHeader("client_name") String clientName
    ) {
        validateClientName(clientName);
        MapShapesResponse response = mapService.createNewMapShapes(request);
        if ("failure".equalsIgnoreCase(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }


    //update API request
    @PatchMapping("/update-mapShapes/{shapeId}")
    public ResponseEntity<MapShapesResponse> updateMapShapes(
            @PathVariable String shapeId,
            @RequestParam String email,
            @RequestBody UpdateMapShapesRequest request,
            @RequestHeader("client_name") String clientName
    ){
        validateClientName(clientName);

        MapShapesResponse response=mapService.updateMapShapes(shapeId,email,request);
        if ("failure".equalsIgnoreCase(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }





    //Delete API request by ID and email by query
    @DeleteMapping("/delete-mapShape/{shapeId}")
    public ResponseEntity<Response> deleteMapShape(
            @PathVariable String shapeId,
            @RequestParam String email,
            @RequestHeader("client_name") String clientName
    ){
        validateClientName(clientName);
        Response response = mapService.deleteMapShapeById(shapeId, email);
        if(response.getStatus().equalsIgnoreCase("failure")){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }




}
