package com.example.ProjectManagement.controller;


import com.example.ProjectManagement.dto.HyperlinkDto.*;
import com.example.ProjectManagement.model.HistoricalYear;
import com.example.ProjectManagement.service.HyperlinkRecordService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project-management-service")
@AllArgsConstructor
@RequiredArgsConstructor
public class HyperlinkRecordController {


    @Autowired
    private  HyperlinkRecordService hyperlinkRecordService;

    //GET request Get Hyperlinks by Latitude, Longitude, year_in_timeline, and Project ID
    @GetMapping("/get-hyperlink-by-lat-long-year")
    public ResponseEntity<GetHyperlinkResponse> getHyperlinksByLatLongYear(
            @RequestParam String projectId,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam long year,
            @RequestParam String era
    ) {
        HistoricalYear yearInTimeline = new HistoricalYear(year, era);
        GetHyperlinkResponse response= hyperlinkRecordService.getHyperlinksByLatLongYear(projectId, latitude, longitude, yearInTimeline);
        if(response.getStatus().equalsIgnoreCase("failure")){
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }


    //Get All Hyperlinks by Project ID and year
    @GetMapping("/get-all-hyperlink-by-project-id-and-year/{projectId}")
    public ResponseEntity<GetHyperlinkResponse> getAllHyperlinksByProjectIdAndYear(
            @PathVariable String projectId,
            @RequestParam long year,
            @RequestParam String era
    ) {
        HistoricalYear yearInTimeline = new HistoricalYear(year, era);
        GetHyperlinkResponse response= hyperlinkRecordService.getAllHyperlinksByProjectIdAndYear(projectId, yearInTimeline);
        if(response.getStatus().equalsIgnoreCase("failure")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }






    //To create a new hyperlinks

    @PostMapping("/create-new-hyperlink")
    public ResponseEntity<HyperlinksResponse> createNewHyperlink(
            @RequestBody CreateHyperlinkRequest request) {
        HyperlinksResponse response = hyperlinkRecordService.createNewHyperlink(request);
        if ("failure".equalsIgnoreCase(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }



    //API to update the hyperlink
    @PatchMapping("/update-hyperlink/{hyperlinkId}")
    public ResponseEntity<HyperlinksResponse> updateHyperlinkById(
            @PathVariable("hyperlinkId") String hyperlinkId,
            @RequestParam("email") String email,
            @RequestBody UpdateHyperlinkRequest request){
        HyperlinksResponse response=hyperlinkRecordService.updateHyperlinkById(hyperlinkId,email,request);
        if ("failure".equalsIgnoreCase(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }


    //to delete the hyperlink
    @DeleteMapping("/delete-hyperlink/{hyperlinkId}")
    public ResponseEntity<NormalResponse> deleteHyperlinkById(
            @PathVariable String hyperlinkId,
            @RequestParam String email
    ){
        NormalResponse response = hyperlinkRecordService.deleteHyperlinkById(hyperlinkId, email);
        if(response.getStatus().equalsIgnoreCase("failure")){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }


}
