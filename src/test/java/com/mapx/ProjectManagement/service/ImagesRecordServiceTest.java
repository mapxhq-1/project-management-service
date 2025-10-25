package com.mapx.ProjectManagement.service;

import com.mapx.ProjectManagement.dto.ImagesDto.ImageGetArrayResponse;
import com.mapx.ProjectManagement.dto.ImagesDto.ImageGetResponse;
import com.mapx.ProjectManagement.dto.ImagesDto.ImageUploadResponse;
import com.mapx.ProjectManagement.dto.NotesDto.Response;
import com.mapx.ProjectManagement.model.Images;
import com.mapx.ProjectManagement.model.Project;
import com.mapx.ProjectManagement.repository.ImagesRecordRepository;
import com.mapx.ProjectManagement.repository.ProjectRecordRepository;
import com.mapx.ProjectManagement.service.ImagesRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ImagesRecordServiceTest {

    @Mock
    private ImagesRecordRepository imagesRepository;

    @Mock
    private ProjectRecordRepository projectRepository;

    @InjectMocks
    private ImagesRecordService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------- checkEra ----------------
    @Test
    void checkEra_valid() {
        assertTrue(service.checkEra("BCE"));
        assertTrue(service.checkEra("CE"));
        assertFalse(service.checkEra("XYZ"));
    }

    // ---------------- getById ----------------
    @Test
    void getById_invalidId_returnsFailure() {
        ImageGetResponse res = service.getById("badId");
        assertEquals("failure", res.getStatus());
        assertEquals("Missing or invalid imageId", res.getMessage());
    }

    @Test
    void getById_notFound_returnsFailure() {
        when(imagesRepository.findById(anyString())).thenReturn(Optional.empty());

        ImageGetResponse res = service.getById(new org.bson.types.ObjectId().toHexString());
        assertEquals("failure", res.getStatus());
        assertEquals("Image not found", res.getMessage());
    }

    @Test
    void getById_found_returnsSuccess() {
        Images img = new Images();
        img.setId("689a233805b4b0da42dfe4c1");
        when(imagesRepository.findById("689a233805b4b0da42dfe4c1")).thenReturn(Optional.of(img));

        ImageGetResponse res = service.getById("689a233805b4b0da42dfe4c1");
        assertEquals("success", res.getStatus());
        assertNull(res.getMessage());
    }

    // ---------------- getImageByLatLong ----------------
    @Test
    void getImageByLatLong_invalidProjectId_returnsFailure() {
        ImageGetArrayResponse res = service.getImageByLatLong("bad", 10, 20);
        assertEquals("failure", res.getStatus());
    }

    @Test
    void getImageByLatLong_notFound_returnsFailure() {
        String pid = new org.bson.types.ObjectId().toHexString();
        when(imagesRepository.findByProjectIdAndLatitudeAndLongitude(pid, 12.3, 45.6))
                .thenReturn(Collections.emptyList());

        ImageGetArrayResponse res = service.getImageByLatLong(pid, 12.3, 45.6);
        assertEquals("failure", res.getStatus());
        assertEquals("No images found", res.getMessage());
    }

    @Test
    void getImageByLatLong_found_returnsSuccess() {
        String pid = new org.bson.types.ObjectId().toHexString();
        Images img = new Images();
        when(imagesRepository.findByProjectIdAndLatitudeAndLongitude(pid, 12.3, 45.6))
                .thenReturn(List.of(img));

        ImageGetArrayResponse res = service.getImageByLatLong(pid, 12.3, 45.6);
        assertEquals("success", res.getStatus());
        assertNotNull(res.getImages());
    }

    // ---------------- getImagesByProjectId ----------------
    @Test
    void getImagesByProjectId_invalid_returnsFailure() {
        ImageGetArrayResponse res = service.getImagesByProjectId("bad");
        assertEquals("failure", res.getStatus());
    }

    @Test
    void getImagesByProjectId_projectNotFound_returnsFailure() {
        String pid = new org.bson.types.ObjectId().toHexString();
        when(projectRepository.findById(pid)).thenReturn(Optional.empty());

        ImageGetArrayResponse res = service.getImagesByProjectId(pid);
        assertEquals("failure", res.getStatus());
        assertEquals("Project not found", res.getMessage());
    }

    @Test
    void getImagesByProjectId_found_returnsSuccess() {
        String pid = new org.bson.types.ObjectId().toHexString();
        when(projectRepository.findById(pid)).thenReturn(Optional.of(new Project()));
        when(imagesRepository.findByProjectId(pid)).thenReturn(List.of(new Images()));

        ImageGetArrayResponse res = service.getImagesByProjectId(pid);
        assertEquals("success", res.getStatus());
    }

    // ---------------- getImageByFileName ----------------
    @Test
    void getImageByFileName_notFound_returns404() throws Exception {
        ResponseEntity<byte[]> res = service.getImageByFileName("notexists.png");
        assertEquals(404, res.getStatusCode().value());
    }

    @Test
    void getImageByFileName_found_returnsBytes() throws Exception {
        // Ensure upload dir exists
        File dir = new File(System.getProperty("user.dir") + "/src/main/resources/image_content/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Create test file inside UPLOAD_DIR
        File temp = new File(dir, "testimg.txt");
        try (FileOutputStream fos = new FileOutputStream(temp)) {
            fos.write("hello".getBytes());
        }

        // Call service
        ResponseEntity<byte[]> res = service.getImageByFileName("testimg.txt");

        // Assertions
        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        assertEquals("hello", new String(res.getBody()));

        // Cleanup
        temp.delete();
    }


    // ---------------- uploadImage ----------------
    @Test
    void uploadImage_invalidProject_returnsFailure() {
        MockMultipartFile file = new MockMultipartFile("f", "a.png", "image/png", "x".getBytes());
        ImageUploadResponse res = service.uploadImage("bad", "a@b.com", "12", "45", file, "cap", "2024", "CE");
        assertEquals("failure", res.getStatus());
    }

    @Test
    void uploadImage_success() {
        String pid = new org.bson.types.ObjectId().toHexString();
        when(projectRepository.findById(pid)).thenReturn(Optional.of(new Project()));
        MockMultipartFile file = new MockMultipartFile("f", "a.png", "image/png", "x".getBytes());

        final Images[] savedImage = new Images[1];
        when(imagesRepository.save(any())).thenAnswer(inv -> {
            savedImage[0] = inv.getArgument(0);
            return savedImage[0];
        });

        ImageUploadResponse res = service.uploadImage(pid, "a@b.com", "12", "45", file, "cap", "2024", "CE");
        assertEquals("success", res.getStatus());

        // ✅ Delete the actual file created
        if (savedImage[0] != null && savedImage[0].getImageFileId()!= null) {
            String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/image_content/";
            File createdFile = new File(UPLOAD_DIR + savedImage[0].getImageFileId()+".png");
            if (createdFile.exists()) {
                createdFile.delete();
            }
        }
    }



    // ---------------- updateImage ----------------
    @Test
    void updateImage_imageNotFound_returnsFailure() {
        String imgId = new org.bson.types.ObjectId().toHexString();
        when(imagesRepository.findById(imgId)).thenReturn(Optional.empty());

        ImageUploadResponse res = service.updateImage(imgId, "a@b.com", null, "cap", "2024", "CE");
        assertEquals("failure", res.getStatus());
    }

    @Test
    void updateImage_emailMismatch_returnsFailure() {
        String imgId = new org.bson.types.ObjectId().toHexString();
        Images img = new Images();
        img.setEmail("other@mail.com");
        when(imagesRepository.findById(imgId)).thenReturn(Optional.of(img));

        ImageUploadResponse res = service.updateImage(imgId, "a@b.com", null, "cap", "2024", "CE");
        assertEquals("failure", res.getStatus());
    }

    // ---------------- deleteImageById ----------------
    @Test
    void deleteImage_invalidId_returnsFailure() {
        Response res = service.deleteImageById("bad", "a@b.com");
        assertEquals("failure", res.getStatus());
    }

    @Test
    void deleteImage_notFound_returnsFailure() {
        String imgId = new org.bson.types.ObjectId().toHexString();
        when(imagesRepository.findById(imgId)).thenReturn(Optional.empty());

        Response res = service.deleteImageById(imgId, "a@b.com");
        assertEquals("failure", res.getStatus());
    }

    @Test
    void deleteImage_success() {
        String imgId = new org.bson.types.ObjectId().toHexString();
        Images img = new Images();
        img.setId(imgId);
        img.setEmail("a@b.com");
        img.setImageFileId("f1");
        img.setFormat("png");
        when(imagesRepository.findById(imgId)).thenReturn(Optional.of(img));

        Response res = service.deleteImageById(imgId, "a@b.com");
        assertEquals("success", res.getStatus());
        verify(imagesRepository, times(1)).deleteById(imgId);
    }
}
