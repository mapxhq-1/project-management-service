package com.example.ProjectManagement.service;


import com.example.ProjectManagement.dto.HyperlinkDto.*;
import com.example.ProjectManagement.dto.ImagesDto.ImageGetArrayResponse;
import com.example.ProjectManagement.model.HistoricalYear;
import com.example.ProjectManagement.model.Hyperlink;
import com.example.ProjectManagement.model.Project;
import com.example.ProjectManagement.repository.HyperlinkRecordRepository;
import com.example.ProjectManagement.repository.ProjectRecordRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class HyperlinkRecordService {


    @Autowired
    private ProjectRecordRepository projectRepository;


    @Autowired
    private  HyperlinkRecordRepository hyperlinksRepository;



    //To check era is correct or not
    public boolean checkEra(String  era){
        // ✅ Using switch for era check
        switch (era) {
            case "BCE", "CE": return true;
            // valid, do nothing
            default:
                break;
        }
        return false;
    }


    //GET request Get Hyperlinks by Latitude, Longitude, year_in_timeline, and Project ID

    public GetHyperlinkResponse getHyperlinksByLatLongYear(
            String projectId,
            double latitude,
            double longitude,
            HistoricalYear yearInTimeline
    ){
        // Validate inputs
        if (projectId == null || projectId.isEmpty()) {
            return new GetHyperlinkResponse("failure", "Invalid or missing projectId", null);
        }
        if (latitude < -90 || latitude > 90) {
            return new GetHyperlinkResponse("failure", "Invalid or missing latitude value", null);
        }
        if (longitude < -180 || longitude > 180) {
            return new GetHyperlinkResponse("failure", "Invalid or missing longitude value", null);
        }
        if (yearInTimeline == null ||
                yearInTimeline.getEra() == null || yearInTimeline.getEra().isEmpty()) {
            return new GetHyperlinkResponse("failure", "Invalid or missing yearInTimeline", null);
        }
        if(!checkEra(yearInTimeline.getEra())){
            return new GetHyperlinkResponse("failure", "Give the correct era BCE or CE", null);
        }

        // Check if project exists
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isEmpty()) {
            return new GetHyperlinkResponse("failure", "Project not found", null);
        }

        // Fetch from DB
        List<Hyperlink> hyperlinks = hyperlinksRepository.findByProjectIdAndLatitudeAndLongitudeAndYearInTimeline(
                projectId, latitude, longitude, yearInTimeline
        );
        if(hyperlinks.isEmpty()){
             return  new GetHyperlinkResponse("failure"," No Hyperlinks are  found",null);
        }
        List<GetHyperlinkResponseDto> hyperlinkDtos =hyperlinks.stream()
                .map(hyperlink -> new GetHyperlinkResponseDto(
                        hyperlink.getId(),
                        hyperlink.getProjectId(),
                        hyperlink.getHyperlinkTitle(),
                        hyperlink.getLatitude(),
                        hyperlink.getLongitude(),
                        hyperlink.getYearInTimeline(),
                        hyperlink.getHyperlink(),
                        hyperlink.getCreatedAt(),
                        hyperlink.getUpdatedAt()
                ))
                .toList();

        return new GetHyperlinkResponse("success", null, hyperlinkDtos);
    }


    //Get All Hyperlinks by Project ID and year

    public GetHyperlinkResponse getAllHyperlinksByProjectIdAndYear(
            String projectId,
            HistoricalYear yearInTimeline
    ){
        if (projectId == null || projectId.isEmpty() || !ObjectId.isValid(projectId)) {
            return new GetHyperlinkResponse("failure", "Missing or invalid projectId", null);
        }
        if (yearInTimeline == null ||
                yearInTimeline.getEra() == null || yearInTimeline.getEra().isEmpty()) {
            return new GetHyperlinkResponse("failure", "Invalid or missing yearInTimeline", null);
        }
        if(!checkEra(yearInTimeline.getEra())){
            return new GetHyperlinkResponse("failure", "Give the correct era BCE or CE", null);
        }
        // Check if project exists
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isEmpty()) {
            return new GetHyperlinkResponse("failure", "Project not found", null);
        }

        // Fetch from DB
        List<Hyperlink> hyperlinks = hyperlinksRepository.findByProjectIdAndYearInTimeline(
                projectId,yearInTimeline
        );

        if (hyperlinks.isEmpty()) {
            return new GetHyperlinkResponse("failure", "No Hyperlinks found", null);
        }
        //Convert to DTO
       List<GetHyperlinkResponseDto> hyperlinkDtos=hyperlinks.stream()
               .map(hyperlink ->new GetHyperlinkResponseDto(
                       hyperlink.getId(),
                       hyperlink.getProjectId(),
                       hyperlink.getHyperlinkTitle(),
                       hyperlink.getLatitude(),
                       hyperlink.getLongitude(),
                       hyperlink.getYearInTimeline(),
                       hyperlink.getHyperlink(),
                       hyperlink.getCreatedAt(),
                       hyperlink.getUpdatedAt()
               ))
               .toList();

        return new GetHyperlinkResponse("success", null, hyperlinkDtos);

    }

    //Create a hyperlink service
    
    public HyperlinksResponse createNewHyperlink(CreateHyperlinkRequest request){
        if (request.getProjectId() == null || request.getProjectId().trim().isEmpty()) {
            return new HyperlinksResponse("failure", "Project ID is required", null);
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return new HyperlinksResponse("failure", "Email is required", null);
        }
        if (request.getHyperlink()== null || request.getHyperlink().trim().isEmpty()) {
            return new HyperlinksResponse("failure", "Hyperlink is required", null);
        }
        if (request.getLatitude() < -90 || request.getLatitude() > 90) {
            return new HyperlinksResponse("failure", "Invalid latitude", null);
        }
        if (request.getLongitude() < -180 || request.getLongitude() > 180) {
            return new HyperlinksResponse("failure", "Invalid longitude", null);
        }
        if(request.getYearInTimeline()!=null) {
            if (request.getYearInTimeline().getEra() != null && (!request.getYearInTimeline().getEra().isEmpty())) {
                if (!checkEra(request.getYearInTimeline().getEra())) {
                    return new HyperlinksResponse("failure", "Invalid Era value", null);
                }
            }
        }

        // Check if project exists
        Optional<Project> project = projectRepository.findById(request.getProjectId());
        if (project.isEmpty()) {
            return new HyperlinksResponse("failure", "Project not found", null);
        }
        try{
            Hyperlink hyperlink=new Hyperlink();
            hyperlink.setProjectId(request.getProjectId());
            hyperlink.setEmail(request.getEmail());
            hyperlink.setHyperlinkTitle(request.getHyperlinkTitle());
            hyperlink.setYearInTimeline(request.getYearInTimeline());
            hyperlink.setLatitude(request.getLatitude());
            hyperlink.setLongitude(request.getLongitude());
            hyperlink.setHyperlink(request.getHyperlink());
            hyperlink.setCreatedAt(Instant.now());
            hyperlink.setUpdatedAt(Instant.now());

            Hyperlink saveHyperlink=hyperlinksRepository.save(hyperlink);
            return  new HyperlinksResponse("success",null,saveHyperlink.getId());
        }  catch (Exception e){
             return new HyperlinksResponse("failure","Error while creating hyperlink: "+e.getMessage(),null);
        }
    }

    //Service code to update the hyperlink
    public HyperlinksResponse updateHyperlinkById(
            String hyperlinkId,
            String email,
            UpdateHyperlinkRequest updateRequest
    ){
        // Validate noteId
        if (hyperlinkId == null || hyperlinkId.isEmpty() || !ObjectId.isValid(hyperlinkId)) {
            return new HyperlinksResponse("failure", "Invalid or missing hyperlink ID", null);
        }

        // Validate email
        if (email == null || email.isEmpty()) {
            return new HyperlinksResponse("failure", "Email is required", null);
        }

        // Validate hyperlink
        if (updateRequest.getHyperlink()== null || updateRequest.getHyperlink().trim().isEmpty()) {
            return new HyperlinksResponse("failure", "hyperlink must not be null or empty", null);
        }

        //validate yearInTimeline
        if(updateRequest.getYearInTimeline()!=null){
            if(updateRequest.getYearInTimeline().getEra()!=null && (!updateRequest.getYearInTimeline().getEra().isEmpty())) {
                if (!checkEra(updateRequest.getYearInTimeline().getEra())) {
                    return new HyperlinksResponse("failure", "Invalid Era value", null);
                }
            }
        }
        // Fetch hyperlink
        Hyperlink hyperlink = hyperlinksRepository.findById(hyperlinkId).orElse(null);
        if (hyperlink == null) {
            return new HyperlinksResponse("failure", "Hyperlink not found", null);
        }

        // Authorization check
        if (!email.equalsIgnoreCase(hyperlink.getEmail())) {
            return new HyperlinksResponse("failure", "Unauthorized: email does not match hyperlink owner", null);
        }
        // Update MongoDB record
        try {
            hyperlink.setHyperlink(updateRequest.getHyperlink());
            hyperlink.setYearInTimeline(updateRequest.getYearInTimeline());
            hyperlink.setUpdatedAt(Instant.now());
            hyperlinksRepository.save(hyperlink);
        } catch (Exception e) {
            return new HyperlinksResponse("failure", "Failed to update Hyperlink record", null);
        }
        return new HyperlinksResponse("success", null, hyperlinkId);

    }




    //Service code to delete the hyperlinks

    public NormalResponse deleteHyperlinkById(String hyperlinkId,String email){

        //validate hyperlinkId
        if(hyperlinkId==null || hyperlinkId.isEmpty() || !ObjectId.isValid(hyperlinkId)){
            return  new NormalResponse("failure", "Invalid or missing hyperlink ID");
        }


        // Validate email
        if (email == null || email.isEmpty()) {
            return new NormalResponse("failure", "Email is required");
        }

        Hyperlink hyperlink = hyperlinksRepository.findById(hyperlinkId).orElse(null);
        if (hyperlink == null) {
            return new NormalResponse("failure", "hyperlink not found");
        }

        if(!email.equalsIgnoreCase(hyperlink.getEmail())){
            return new NormalResponse("failure", "Unauthorized: email does not match hyperlink owner");

        }

        try{
            hyperlinksRepository.deleteById(hyperlinkId);
            return  new NormalResponse("success",null);
        } catch (Exception e){
            return new NormalResponse("failure","Failed to delete hyperlink record");
        }

    }



}
