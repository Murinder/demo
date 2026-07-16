package com.example.eventservice.integration;

import com.example.eventservice.dto.CreateEventDto;
import com.example.eventservice.dto.UpdateEventDto;
import com.example.eventservice.model.Event;
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
class EventControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventApplicationRepository eventApplicationRepository;

    @Autowired
    private TeamRepository teamRepository;

    private static final UUID LECTURER_ID = UUID.randomUUID();
    private static final UUID ADMIN_ID = UUID.randomUUID();
    private static final UUID STUDENT_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        eventApplicationRepository.deleteAll();
        teamRepository.deleteAll();
        eventRepository.deleteAll();
    }

    private CreateEventDto buildCreateEventDto() {
        return CreateEventDto.builder()
                .title("Test Hackathon")
                .description("A test hackathon event")
                .startDate(OffsetDateTime.now().plusDays(7))
                .endDate(OffsetDateTime.now().plusDays(9))
                .format(EventFormat.OFFLINE)
                .createdBy(LECTURER_ID)
                .location("University Hall A")
                .maxParticipants(100)
                .registrationDeadline(OffsetDateTime.now().plusDays(5))
                .build();
    }

    private Event createEventInDb() {
        return eventRepository.save(Event.builder()
                .id(UUID.randomUUID())
                .title("Existing Event")
                .description("An existing event")
                .startDate(OffsetDateTime.now().plusDays(7))
                .endDate(OffsetDateTime.now().plusDays(9))
                .format(EventFormat.ONLINE)
                .status(EventStatus.DRAFT)
                .createdBy(LECTURER_ID)
                .build());
    }

    @Test
    void createEvent_AsLecturer_Returns201() throws Exception {
        CreateEventDto dto = buildCreateEventDto();

        mockMvc.perform(TestSecurityHelper.withLecturer(
                        post("/api/v1/events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)),
                        LECTURER_ID))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Hackathon"))
                .andExpect(jsonPath("$.format").value("OFFLINE"));

        assertThat(eventRepository.count()).isEqualTo(1);
    }

    @Test
    void createEvent_AsStudent_Returns403() throws Exception {
        CreateEventDto dto = buildCreateEventDto();

        mockMvc.perform(TestSecurityHelper.withStudent(
                        post("/api/v1/events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)),
                        STUDENT_ID))
                .andExpect(status().isForbidden());

        assertThat(eventRepository.count()).isEqualTo(0);
    }

    @Test
    void getAllEvents_ReturnsEventList() throws Exception {
        createEventInDb();
        createEventInDb();

        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/events"),
                        STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getEventById_Returns200() throws Exception {
        Event event = createEventInDb();

        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/events/" + event.getId()),
                        STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Existing Event"));
    }

    @Test
    void updateEvent_AsLecturer_Returns200() throws Exception {
        Event event = createEventInDb();

        UpdateEventDto updateDto = UpdateEventDto.builder()
                .title("Updated Hackathon")
                .status(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(TestSecurityHelper.withLecturer(
                        put("/api/v1/events/" + event.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDto)),
                        LECTURER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Hackathon"))
                .andExpect(jsonPath("$.status").value("PUBLISHED"));
    }

    @Test
    void deleteEvent_AsAdmin_Returns204() throws Exception {
        Event event = createEventInDb();

        mockMvc.perform(TestSecurityHelper.withAdmin(
                        delete("/api/v1/events/" + event.getId()),
                        ADMIN_ID))
                .andExpect(status().isNoContent());

        assertThat(eventRepository.findById(event.getId())).isEmpty();
    }

    @Test
    void deleteEvent_AsStudent_Returns403() throws Exception {
        Event event = createEventInDb();

        mockMvc.perform(TestSecurityHelper.withStudent(
                        delete("/api/v1/events/" + event.getId()),
                        STUDENT_ID))
                .andExpect(status().isForbidden());

        assertThat(eventRepository.findById(event.getId())).isPresent();
    }

    @Test
    void updateEvent_AsStudent_Returns403() throws Exception {
        Event event = createEventInDb();

        UpdateEventDto updateDto = UpdateEventDto.builder()
                .title("Should Not Update")
                .build();

        mockMvc.perform(TestSecurityHelper.withStudent(
                        put("/api/v1/events/" + event.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDto)),
                        STUDENT_ID))
                .andExpect(status().isForbidden());
    }
}
