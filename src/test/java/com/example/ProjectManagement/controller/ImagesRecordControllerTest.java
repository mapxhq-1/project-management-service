package com.example.ProjectManagement.controller;

import com.example.ProjectManagement.dto.ImagesDto.ImageGetArrayResponse;
import com.example.ProjectManagement.dto.ImagesDto.ImageGetResponse;
import com.example.ProjectManagement.dto.ImagesDto.ImageUploadResponse;
import com.example.ProjectManagement.dto.NotesDto.Response;
import com.example.ProjectManagement.model.Images;   // <-- import your model
import com.example.ProjectManagement.service.ImagesRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImagesRecordControllerTest {

    @InjectMocks
    private ImagesRecordController controller;

    @Mock
    private ImagesRecordService imageService;

    private ImageGetResponse imageGetResponse;
    private ImageGetArrayResponse arrayResponse;
    private ImageUploadResponse uploadResponse;
    private Response deleteResponse;

    @BeforeEach
    void setup() {
        // ✅ Use mocks of real model classes instead of Object
        Images mockImage = mock(Images.class);

        imageGetResponse = new ImageGetResponse("success", null, mockImage);
        arrayResponse = new ImageGetArrayResponse("success", null, List.of(mockImage));
        uploadResponse = new ImageUploadResponse("success", null, "image123");
        deleteResponse = new Response("success", null);
    }

    @Test
    void testGetById_success() {
        when(imageService.getById("123")).thenReturn(imageGetResponse);

        ResponseEntity<ImageGetResponse> response = controller.getById("123");

        assertEquals("success", response.getBody().getStatus());
        verify(imageService).getById("123");
    }

    @Test
    void testGetImageByLatLong_success() {
        when(imageService.getImageByLatLong("proj1", 10.0, 20.0)).thenReturn(arrayResponse);

        ResponseEntity<ImageGetArrayResponse> response =
                controller.getImageByLatLong("proj1", 10.0, 20.0);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("success", response.getBody().getStatus());
    }

    @Test
    void testGetImageByProjectId_success() {
        when(imageService.getImagesByProjectId("proj1")).thenReturn(arrayResponse);

        ResponseEntity<ImageGetArrayResponse> response = controller.getImageByProjectId("proj1");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("success", response.getBody().getStatus());
    }

    @Test
    void testGetImageByProjectId_failure() {
        ImageGetArrayResponse failResponse = new ImageGetArrayResponse("failure", "Invalid project", null);
        when(imageService.getImagesByProjectId("projX")).thenReturn(failResponse);

        ResponseEntity<ImageGetArrayResponse> response = controller.getImageByProjectId("projX");

        assertEquals(400,response.getStatusCode().value());
        assertEquals("failure", response.getBody().getStatus());
    }

    @Test
    void testGetImageByFileName_success() throws IOException {
        byte[] data = "image-bytes".getBytes();
        ResponseEntity<byte[]> entity = ResponseEntity.ok(data);

        when(imageService.getImageByFileName("img.png")).thenReturn(entity);

        ResponseEntity<byte[]> response = controller.getImageByFileName("img.png");

        assertEquals(200,response.getStatusCode().value());
        assertArrayEquals(data, response.getBody());
    }

    @Test
    void testUploadImage_success() {
        MultipartFile mockFile = mock(MultipartFile.class);

        when(imageService.uploadImage("proj1", "user@mail.com", "10", "20",
                mockFile, "caption", "2025", "CE")).thenReturn(uploadResponse);

        ImageUploadResponse response = controller.uploadImage("proj1", "user@mail.com", "10", "20",
                mockFile, "caption", "2025", "CE");

        assertEquals("success", response.getStatus());
        assertEquals("image123", response.getImageId());
    }

    @Test
    void testUpdateImage_success() {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(imageService.updateImage("123", "user@mail.com", mockFile, "new caption", "2025", "CE"))
                .thenReturn(uploadResponse);

        ImageUploadResponse response = controller.updateImage("123", "user@mail.com", mockFile,
                "new caption", "2025", "CE");

        assertEquals("success", response.getStatus());
        assertEquals("image123", response.getImageId());
    }

    @Test
    void testDeleteImageById_success() {
        when(imageService.deleteImageById("123", "user@mail.com")).thenReturn(deleteResponse);

        Response response = controller.deleteImageById("123", "user@mail.com");

        assertEquals("success", response.getStatus());
        verify(imageService).deleteImageById("123", "user@mail.com");
    }
}
