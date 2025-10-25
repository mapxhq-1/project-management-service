package com.mapx.ProjectManagement.service;

import com.mapx.ProjectManagement.dto.HyperlinkDto.*;
import com.mapx.ProjectManagement.model.HistoricalYear;
import com.mapx.ProjectManagement.model.Hyperlink;
import com.mapx.ProjectManagement.model.Project;
import com.mapx.ProjectManagement.repository.HyperlinkRecordRepository;
import com.mapx.ProjectManagement.repository.ProjectRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HyperlinkRecordServiceTest {

    @Mock
    private ProjectRecordRepository projectRepository;

    @Mock
    private HyperlinkRecordRepository hyperlinksRepository;

    @InjectMocks
    private HyperlinkRecordService service;

    private  String validId = "507f1f77bcf86cd799439011"; // ✅ valid 24-char hex ObjectId

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- checkEra ---
    @Test
    void checkEra_validEra_returnsTrue() {
        assertTrue(service.checkEra("BCE"));
        assertTrue(service.checkEra("CE"));
    }

    @Test
    void checkEra_invalidEra_returnsFalse() {
        assertFalse(service.checkEra("XYZ"));
    }

    // --- getHyperlinksByLatLongYear ---
    @Test
    void getHyperlinksByLatLongYear_invalidProjectId_returnsFailure() {
        GetHyperlinkResponse res = service.getHyperlinksByLatLongYear("", 10, 20, new HistoricalYear(2000, "CE"));
        assertEquals("failure", res.getStatus());
    }

    @Test
    void getHyperlinksByLatLongYear_projectNotFound_returnsFailure() {
        when(projectRepository.findById("proj1")).thenReturn(Optional.empty());

        GetHyperlinkResponse res = service.getHyperlinksByLatLongYear("proj1", 10, 20, new HistoricalYear(2000, "CE"));

        assertEquals("failure", res.getStatus());
        assertEquals("Project not found", res.getMessage());
    }

    @Test
    void getHyperlinksByLatLongYear_success() {
        when(projectRepository.findById("proj1")).thenReturn(Optional.of(new Project()));
        Hyperlink link = new Hyperlink();
        link.setId("id1");
        link.setProjectId("proj1");
        link.setLatitude(10.98);
        link.setLongitude(20.98);
        link.setYearInTimeline(new HistoricalYear(2000, "CE"));
        link.setHyperlink("http://test.com");
        link.setCreatedAt(Instant.now());
        link.setUpdatedAt(Instant.now());

        when(hyperlinksRepository.findByProjectIdAndLatitudeAndLongitudeAndYearInTimeline(
                anyString(), anyDouble(), anyDouble(), any(HistoricalYear.class)))
                .thenReturn(List.of(link));

        GetHyperlinkResponse res = service.getHyperlinksByLatLongYear("proj1", 10.98, 20.98, new HistoricalYear(2000, "CE"));

        assertEquals("success", res.getStatus());
        assertNull(res.getMessage());
        assertNotNull(res.getHyperlinks());

    }

    // --- getAllHyperlinksByProjectIdAndYear ---
    @Test
    void getAllHyperlinksByProjectIdAndYear_InvalidProjectId_returnsFailure() {
        when(projectRepository.findById("proj1")).thenReturn(Optional.empty());

        GetHyperlinkResponse res = service.getAllHyperlinksByProjectIdAndYear("proj1", new HistoricalYear(2000, "CE"));

        assertEquals("failure", res.getStatus());
        assertEquals("Missing or invalid projectId", res.getMessage());
    }

    @Test
    void getAllHyperlinksByProjectIdAndYear_ProjectNotFound_returnsFailure() {
        when(projectRepository.findById(validId)).thenReturn(Optional.empty());

        GetHyperlinkResponse res = service.getAllHyperlinksByProjectIdAndYear(validId, new HistoricalYear(2000, "CE"));

        assertEquals("failure", res.getStatus());
        assertEquals("Project not found", res.getMessage());
    }

    @Test
    void getAllHyperlinksByProjectIdAndYear_success() {
        when(projectRepository.findById(validId)).thenReturn(Optional.of(new Project()));
        Hyperlink link = new Hyperlink();
        link.setId("id1");
        link.setProjectId(validId);
        link.setLatitude(10.98);
        link.setLongitude(20.98);
        link.setYearInTimeline(new HistoricalYear(2000, "CE"));
        link.setHyperlink("http://test.com");
        link.setCreatedAt(Instant.now());
        link.setUpdatedAt(Instant.now());

        when(hyperlinksRepository.findByProjectIdAndYearInTimeline(anyString(), any(HistoricalYear.class)))
                .thenReturn(List.of(link));

        GetHyperlinkResponse res = service.getAllHyperlinksByProjectIdAndYear(validId, new HistoricalYear(2000, "CE"));

        assertEquals("success", res.getStatus());
        assertNull(res.getMessage());
        assertNotNull(res.getHyperlinks());
    }

    // --- createNewHyperlink ---
    @Test
    void createNewHyperlink_missingProjectId_returnsFailure() {
        CreateHyperlinkRequest req = new CreateHyperlinkRequest();
        req.setEmail("test@email.com");
        req.setHyperlink("http://test.com");

        HyperlinksResponse res = service.createNewHyperlink(req);
        assertEquals("failure", res.getStatus());
    }

    @Test
    void createNewHyperlink_projectNotFound_returnsFailure() {
        CreateHyperlinkRequest req = new CreateHyperlinkRequest();
        req.setProjectId("proj1");
        req.setEmail("test@email.com");
        req.setHyperlink("http://test.com");
        req.setLatitude(10);
        req.setLongitude(20);

        when(projectRepository.findById("proj1")).thenReturn(Optional.empty());

        HyperlinksResponse res = service.createNewHyperlink(req);
        assertEquals("failure", res.getStatus());
        assertEquals("Project not found", res.getMessage());
    }

    @Test
    void createNewHyperlink_success() {
        CreateHyperlinkRequest req = new CreateHyperlinkRequest();
        req.setProjectId("proj1");
        req.setEmail("test@email.com");
        req.setHyperlink("http://test.com");
        req.setLatitude(10);
        req.setLongitude(20);
        req.setYearInTimeline(new HistoricalYear(2000, "CE"));

        when(projectRepository.findById("proj1")).thenReturn(Optional.of(new Project()));
        Hyperlink saved = new Hyperlink();
        saved.setId("id1");
        when(hyperlinksRepository.save(any(Hyperlink.class))).thenReturn(saved);

        HyperlinksResponse res = service.createNewHyperlink(req);
        assertEquals("success", res.getStatus());
        assertEquals("id1", res.getHyperlinkId());
    }

    // --- updateHyperlinkById ---
    @Test
    void updateHyperlinkById_notFound_returnsFailure() {
        UpdateHyperlinkRequest req = new UpdateHyperlinkRequest();
        req.setHyperlink("http://updated.com");

        when(hyperlinksRepository.findById("id1")).thenReturn(Optional.empty());

        HyperlinksResponse res = service.updateHyperlinkById("id1", "email@test.com", req);
        assertEquals("failure", res.getStatus());
    }

    @Test
    void updateHyperlinkById_success() {
        UpdateHyperlinkRequest req = new UpdateHyperlinkRequest();
        req.setHyperlink("http://updated.com");
        req.setYearInTimeline(new HistoricalYear(2020, "CE"));

        Hyperlink link = new Hyperlink();
        link.setId(validId);
        link.setEmail("email@test.com");

        when(hyperlinksRepository.findById(validId)).thenReturn(Optional.of(link));

        HyperlinksResponse res = service.updateHyperlinkById(validId, "email@test.com", req);

        assertEquals("success", res.getStatus());
        assertNull(res.getMessage());
        assertEquals(validId,res.getHyperlinkId());
    }

    // --- deleteHyperlinkById ---
    @Test
    void deleteHyperlinkById_notFound_returnsFailure() {
        when(hyperlinksRepository.findById("id1")).thenReturn(Optional.empty());

        NormalResponse res = service.deleteHyperlinkById("id1", "test@email.com");
        assertEquals("failure", res.getStatus());
    }

    @Test
    void deleteHyperlinkById_success() {


        Hyperlink link = new Hyperlink();
        link.setId(validId);
        link.setEmail("test@email.com");

        when(hyperlinksRepository.findById(validId)).thenReturn(Optional.of(link));

        NormalResponse res = service.deleteHyperlinkById(validId, "test@email.com");

        assertEquals("success", res.getStatus());
        assertNull(res.getMessage());
    }

}
