package com.example.ProjectManagement.service;


import com.example.ProjectManagement.dto.CloneProjectDto.CloneProjectResponse;
import com.example.ProjectManagement.model.*;
import com.example.ProjectManagement.repository.*;
import com.example.ProjectManagement.Exception.InvalidProjectIdException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectCloneService {

    @Autowired
    private ProjectRecordRepository projectRepository;

    @Autowired
    private NotesRecordRepository notesRepository;

    @Autowired
    private ImagesRecordRepository imagesRepository;

    @Autowired
    private HyperlinkRecordRepository hyperlinkRepository;

    @Autowired
    private MapShapesRepository mapRepostory;


    @Autowired
    private ObjectMapper objectMapper;


    //Exception handling for invalid project id which is not present in database





    private static final String HTML_NOTES_DIR = "src/main/resources/html_notes/";
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/image_content/";

    private static final String UPLOAD_MAPSHAPES = System.getProperty("user.dir") + "/src/main/resources/map_shapes_content/";

    public CloneProjectResponse CloneProject(
          String requestEmail,
          String projectId
    ) {
        // ✅ Validate projectId
        if (projectId == null || projectId.isEmpty() || !ObjectId.isValid(projectId)) {
            return new CloneProjectResponse(null, "failure: INVALID_PROJECT_ID");
        }
        // ✅ Fetch original project or return failure if not found
        Project original = projectRepository.findById(projectId)
                .orElseThrow(() -> new InvalidProjectIdException());

//        if (original == null) {
//            return new CloneProjectResponse(null, "failure: INVALID_PROJECT_ID");
//        }

//        // ✅ Validate ownership/access
//        if (!original.getOwnerEmail().equalsIgnoreCase(requestEmail)
//                && !original.getAccessorList().contains(requestEmail)) {
//            return new CloneProjectResponse(null, "failure: UNAUTHORIZED_ACCESS");
//        }
        try {
            Project cloneProject = objectMapper.convertValue(original, Project.class);

            cloneProject.setId(null);
            cloneProject.setOwnerEmail(requestEmail);
            cloneProject.setAccessorList(Collections.emptyList());
            cloneProject.setUpdatedAt(Instant.now());
            cloneProject.setCreatedAt(Instant.now());
            cloneProject = projectRepository.save(cloneProject);
            String clonedProjectId = cloneProject.getId();
            // ✅ Clone associated resources
            cloneNotes(projectId, clonedProjectId, requestEmail);
            cloneImages(projectId, clonedProjectId, requestEmail);
            cloneHyperlinks(projectId, clonedProjectId, requestEmail);
            cloneMapShapes(projectId, clonedProjectId, requestEmail);
            return new CloneProjectResponse(clonedProjectId, "success");
        }catch (IOException e) {
            return new CloneProjectResponse(null, "failure: FILE_COPY_ERROR");
        } catch (Exception e) {
            return new CloneProjectResponse(null, "failure: INTERNAL_ERROR");
        }
    }

    public void cloneNotes(String originalProjectId, String clonedProjectId, String requestEmail) throws IOException{
        List<Notes> originalNotes=notesRepository.findByProjectId(originalProjectId);
        for(Notes note:originalNotes){
                String oldFileName=note.getHtmlFileId()+".html";
                String newFileUuid= UUID.randomUUID().toString();
                String newFileName = newFileUuid + ".html";
                Files.copy(Paths.get(HTML_NOTES_DIR+oldFileName),Paths.get(HTML_NOTES_DIR+newFileName));
                Notes clonedNote = new Notes();
                clonedNote.setProjectId(clonedProjectId);
                clonedNote.setEmail(requestEmail);
                clonedNote.setLatitude(note.getLatitude());
                clonedNote.setYearInTimeline(note.getYearInTimeline());
                clonedNote.setLongitude(note.getLongitude());
                clonedNote.setNoteTitle(note.getNoteTitle());
                clonedNote.setHtmlFileId(newFileUuid);
                clonedNote.setUpdatedAt(Instant.now());
                clonedNote.setCreatedAt(Instant.now());
                notesRepository.save(clonedNote);

        }
    }

    public void cloneImages(String originalProjectId, String clonedProjectId, String requestEmail)throws IOException {
        List<Images> originalImages = imagesRepository.findByProjectId(originalProjectId);
        for (Images image : originalImages) {

                String oldFileName = image.getImageFileId() + "." + image.getFormat();
                String newFileUuid = UUID.randomUUID().toString();
                String newFileName = newFileUuid + "." + image.getFormat();

                Files.copy(Paths.get(UPLOAD_DIR + oldFileName),
                        Paths.get(UPLOAD_DIR + newFileName));
                Images clonedImage = new Images();
                clonedImage.setProjectId(clonedProjectId);
                clonedImage.setEmail(requestEmail);
                clonedImage.setImageFileId(newFileUuid);
                clonedImage.setCaption(image.getCaption());
                clonedImage.setFormat(image.getFormat());
                clonedImage.setLatitude(image.getLatitude());
                clonedImage.setLongitude(image.getLongitude());
                clonedImage.setYearInTimeline(image.getYearInTimeline());
                clonedImage.setCreatedAt(Instant.now());
                clonedImage.setUpdatedAt(Instant.now());

                imagesRepository.save(clonedImage);
        }
    }

    public void cloneHyperlinks(String originalProjectId, String clonedProjectId, String requestEmail) {
        List<Hyperlink> originalLinks = hyperlinkRepository.findByProjectId(originalProjectId);
        for (Hyperlink link : originalLinks) {
            Hyperlink clonedLink = new Hyperlink();
            clonedLink.setProjectId(clonedProjectId);
            clonedLink.setEmail(requestEmail);
            clonedLink.setHyperlinkTitle(link.getHyperlinkTitle());
            clonedLink.setYearInTimeline(link.getYearInTimeline());
            clonedLink.setLatitude(link.getLatitude());
            clonedLink.setLongitude(link.getLongitude());
            clonedLink.setHyperlink(link.getHyperlink());
            clonedLink.setCreatedAt(Instant.now());
            clonedLink.setUpdatedAt(Instant.now());
            hyperlinkRepository.save(clonedLink);
        }
    }


    // ... (keep your existing cloneNotes, cloneImages, cloneHyperlinks methods) ...

    public void cloneMapShapes(String originalProjectId, String clonedProjectId, String requestEmail) throws IOException {
        List<MapShapes> originalShapes = mapRepostory.findByProjectId(originalProjectId);

        for (MapShapes originalShape : originalShapes) {
            // Create a new MapShapes object
            MapShapes clonedShape = new MapShapes();

            // Copy the geojson file
            String oldFileId = originalShape.getFileId();
            if (oldFileId != null && !oldFileId.isEmpty()) {
                String newFileUuid = UUID.randomUUID().toString();
                Files.copy(Paths.get(UPLOAD_MAPSHAPES + oldFileId + ".json"),
                        Paths.get(UPLOAD_MAPSHAPES + newFileUuid + ".json"));
                clonedShape.setFileId(newFileUuid);
            }

            // Set the new properties
            clonedShape.setProjectId(clonedProjectId);
            clonedShape.setEmail(requestEmail);
            clonedShape.setYearInTimeline(originalShape.getYearInTimeline());
            clonedShape.setCreatedAt(Instant.now());
            clonedShape.setUpdatedAt(Instant.now());

            // Save the new MapShape to the database
            mapRepostory.save(clonedShape);
        }
    }


}
