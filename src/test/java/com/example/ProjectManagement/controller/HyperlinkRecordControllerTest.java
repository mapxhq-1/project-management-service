package com.example.ProjectManagement.controller;

import com.example.ProjectManagement.dto.HyperlinkDto.*;
import com.example.ProjectManagement.model.HistoricalYear;
import com.example.ProjectManagement.service.HyperlinkRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HyperlinkRecordControllerTest {

    @Mock
    private HyperlinkRecordService hyperlinkRecordService;

    @InjectMocks
    private HyperlinkRecordController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- GET by lat/long/year ---
    @Test
    void getHyperlinksByLatLongYear_success() {
        GetHyperlinkResponse mockResponse = new GetHyperlinkResponse("success", null, null);
        when(hyperlinkRecordService.getHyperlinksByLatLongYear(
                anyString(), anyDouble(), anyDouble(), any(HistoricalYear.class)))
                .thenReturn(mockResponse);

        ResponseEntity<GetHyperlinkResponse> response =
                controller.getHyperlinksByLatLongYear("proj1", 12.34, 56.78, 2000, "CE","mapx");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("success", response.getBody().getStatus());
    }

    @Test
    void getHyperlinksByLatLongYear_failure() {
        GetHyperlinkResponse mockResponse = new GetHyperlinkResponse("failure", "Invalid input", null);
        when(hyperlinkRecordService.getHyperlinksByLatLongYear(
                anyString(), anyDouble(), anyDouble(), any(HistoricalYear.class)))
                .thenReturn(mockResponse);

        ResponseEntity<GetHyperlinkResponse> response =
                controller.getHyperlinksByLatLongYear("proj1", 12.34, 56.78, 2000, "CE","mapx");

        assertEquals(400, response.getStatusCode().value());
        assertEquals("failure", response.getBody().getStatus());
    }

    // --- GET all by projectId and year ---
    @Test
    void getAllHyperlinksByProjectIdAndYear_success() {
        GetHyperlinkResponse mockResponse = new GetHyperlinkResponse("success", null, null);
        when(hyperlinkRecordService.getAllHyperlinksByProjectIdAndYear(anyString(), any(HistoricalYear.class)))
                .thenReturn(mockResponse);

        ResponseEntity<GetHyperlinkResponse> response =
                controller.getAllHyperlinksByProjectIdAndYear("proj1", 2020, "CE","mapx");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("success", response.getBody().getStatus());
    }

    @Test
    void getAllHyperlinksByProjectIdAndYear_failure() {
        GetHyperlinkResponse mockResponse = new GetHyperlinkResponse("failure", "Not found", null);
        when(hyperlinkRecordService.getAllHyperlinksByProjectIdAndYear(anyString(), any(HistoricalYear.class)))
                .thenReturn(mockResponse);

        ResponseEntity<GetHyperlinkResponse> response =
                controller.getAllHyperlinksByProjectIdAndYear("proj1", 2020, "CE","mapx");

        assertEquals(400, response.getStatusCode().value());
        assertEquals("failure", response.getBody().getStatus());
    }

    // --- POST create new hyperlink ---
    @Test
    void createNewHyperlink_success() {
        CreateHyperlinkRequest req = new CreateHyperlinkRequest();
        HyperlinksResponse mockResponse = new HyperlinksResponse("success", null, "id1");
        when(hyperlinkRecordService.createNewHyperlink(any(CreateHyperlinkRequest.class)))
                .thenReturn(mockResponse);

        ResponseEntity<HyperlinksResponse> response = controller.createNewHyperlink(req,"mapx");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("success", response.getBody().getStatus());
    }

    @Test
    void createNewHyperlink_failure() {
        CreateHyperlinkRequest req = new CreateHyperlinkRequest();
        HyperlinksResponse mockResponse = new HyperlinksResponse("failure", "Bad input", null);
        when(hyperlinkRecordService.createNewHyperlink(any(CreateHyperlinkRequest.class)))
                .thenReturn(mockResponse);

        ResponseEntity<HyperlinksResponse> response = controller.createNewHyperlink(req,"mapx");

        assertEquals(400, response.getStatusCode().value());
        assertEquals("failure", response.getBody().getStatus());
    }

    // --- PATCH update hyperlink ---
    @Test
    void updateHyperlinkById_success() {
        UpdateHyperlinkRequest req = new UpdateHyperlinkRequest();
        HyperlinksResponse mockResponse = new HyperlinksResponse("success", null, "id1");
        when(hyperlinkRecordService.updateHyperlinkById(anyString(), anyString(), any(UpdateHyperlinkRequest.class)))
                .thenReturn(mockResponse);

        ResponseEntity<HyperlinksResponse> response =
                controller.updateHyperlinkById("id1", "test@email.com", req,"mapx");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("success", response.getBody().getStatus());
    }

    @Test
    void updateHyperlinkById_failure() {
        UpdateHyperlinkRequest req = new UpdateHyperlinkRequest();
        HyperlinksResponse mockResponse = new HyperlinksResponse("failure", "Unauthorized", null);
        when(hyperlinkRecordService.updateHyperlinkById(anyString(), anyString(), any(UpdateHyperlinkRequest.class)))
                .thenReturn(mockResponse);

        ResponseEntity<HyperlinksResponse> response =
                controller.updateHyperlinkById("id1", "wrong@email.com", req,"mapx");

        assertEquals(400, response.getStatusCode().value());
        assertEquals("failure", response.getBody().getStatus());
    }

    // --- DELETE hyperlink ---
    @Test
    void deleteHyperlinkById_success() {
        NormalResponse mockResponse = new NormalResponse("success", null);
        when(hyperlinkRecordService.deleteHyperlinkById(anyString(), anyString()))
                .thenReturn(mockResponse);

        ResponseEntity<NormalResponse> response =
                controller.deleteHyperlinkById("id1", "test@email.com","mapx");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("success", response.getBody().getStatus());
    }

    @Test
    void deleteHyperlinkById_failure() {
        NormalResponse mockResponse = new NormalResponse("failure", "Not found");
        when(hyperlinkRecordService.deleteHyperlinkById(anyString(), anyString()))
                .thenReturn(mockResponse);

        ResponseEntity<NormalResponse> response =
                controller.deleteHyperlinkById("id1", "wrong@email.com","mapx");

        assertEquals(400, response.getStatusCode().value());
        assertEquals("failure", response.getBody().getStatus());
    }
}
