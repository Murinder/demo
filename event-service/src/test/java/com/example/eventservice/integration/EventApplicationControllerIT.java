package com.example.eventservice.integration;

import com.example.eventservice.dto.CreateEventApplicationDto;
import com.example.eventservice.dto.UpdateEventApplicationDto;
import com.example.eventservice.model.Event;
import com.example.eventservice.model.EventApplication;
import com.example.eventservice.model.enums.ApplicationStatus;
import com.example.eventservice.repository.EventApplicationRepository;
import com.example.eventservice.repository.EventRepository;
import com.example.eventservice.repository.TeamRepository;
import com.example.sharedlib.enums.EventFormat;
import com.example.sharedlib.enums.EventStatus;
import com.example.testsupport.BaseIntegrationTest;
import com.example.testsupport.security.TestSecurityHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class EventApplicationControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventApplicationRepository applicationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TeamRepository teamRepository;

    private static final UUID LECTURER_ID = UUID.randomUUID();
    private static final UUID STUDENT_ID = UUID.randomUUID();

    private Event testEvent;

    @BeforeEach
    void setUp() {
        applicationRepository.deleteAll();
        teamRepository.deleteAll();
        eventRepository.deleteAll();

        testEvent = eventRepository.save(Event.builder()
                .id(UUID.randomUUID())
                .title("Hackathon 2026")
                .description("Annual hackathon")
                .startDate(OffsetDateTime.now().plusDays(14))
                .endDate(OffsetDateTime.now().plusDays(16))
                .format(EventFormat.HYBRID)
                .status(EventStatus.REGISTRATION_OPEN)
                .createdBy(LECTURER_ID)
                .build());
    }

    @Test
    void createApplication_AsStudent_Returns201() throws Exception {
        CreateEventApplicationDto dto = CreateEventApplicationDto.builder()
                .eventId(testEvent.getId())
                .userId(STUDENT_ID)
                .build();

        mockMvc.perform(TestSecurityHelper.withStudent(
                        post("/api/v1/event-applications")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)),
                        STUDENT_ID))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventId").value(testEvent.getId().toString()))
                .andExpect(jsonPath("$.userId").value(STUDENT_ID.toString()))
                .andExpect(jsonPath("$.status").value("SUBMITTED"));

        assertThat(applicationRepository.count()).isEqualTo(1);
    }

    @Test
    void getApplicationById_Returns200() throws Exception {
        EventApplication application = applicationRepository.save(EventApplication.builder()
                .event(testEvent)
                .userId(STUDENT_ID)
                .status(ApplicationStatus.SUBMITTED)
                .motivation("I want to participate")
                .build());

        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/event-applications/" + application.getId()),
                        STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(STUDENT_ID.toString()))
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
    }

    @Test
    void getAllApplications_AsLecturer_ReturnsList() throws Exception {
        applicationRepository.save(EventApplication.builder()
                .event(testEvent)
                .userId(UUID.randomUUID())
                .status(ApplicationStatus.SUBMITTED)
                .build());
        applicationRepository.save(EventApplication.builder()
                .event(testEvent)
                .userId(UUID.randomUUID())
                .status(ApplicationStatus.SUBMITTED)
                .build());

        mockMvc.perform(TestSecurityHelper.withLecturer(
                        get("/api/v1/event-applications"),
                        LECTURER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllApplications_AsStudent_Returns403() throws Exception {
        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/event-applications"),
                        STUDENT_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateApplication_AsLecturer_ApprovesApplication() throws Exception {
        EventApplication application = applicationRepository.save(EventApplication.builder()
                .event(testEvent)
                .userId(STUDENT_ID)
                .status(ApplicationStatus.SUBMITTED)
                .build());

        UpdateEventApplicationDto updateDto = UpdateEventApplicationDto.builder()
                .status(ApplicationStatus.APPROVED)
                .build();

        mockMvc.perform(TestSecurityHelper.withLecturer(
                        put("/api/v1/event-applications/" + application.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDto)),
                        LECTURER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        EventApplication updated = applicationRepository.findById(application.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(ApplicationStatus.APPROVED);
    }

    @Test
    void updateApplication_RejectApplication() throws Exception {
        EventApplication application = applicationRepository.save(EventApplication.builder()
                .event(testEvent)
                .userId(STUDENT_ID)
                .status(ApplicationStatus.SUBMITTED)
                .build());

        UpdateEventApplicationDto updateDto = UpdateEventApplicationDto.builder()
                .status(ApplicationStatus.REJECTED)
                .build();

        mockMvc.perform(TestSecurityHelper.withLecturer(
                        put("/api/v1/event-applications/" + application.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDto)),
                        LECTURER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void deleteApplication_AsLecturer_Returns204() throws Exception {
        EventApplication application = applicationRepository.save(EventApplication.builder()
                .event(testEvent)
                .userId(STUDENT_ID)
                .status(ApplicationStatus.SUBMITTED)
                .build());

        mockMvc.perform(TestSecurityHelper.withLecturer(
                        delete("/api/v1/event-applications/" + application.getId()),
                        LECTURER_ID))
                .andExpect(status().isNoContent());

        assertThat(applicationRepository.findById(application.getId())).isEmpty();
    }
}
