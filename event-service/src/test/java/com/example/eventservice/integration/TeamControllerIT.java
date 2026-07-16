package com.example.eventservice.integration;

import com.example.eventservice.dto.CreateTeamDto;
import com.example.eventservice.dto.UpdateTeamDto;
import com.example.eventservice.model.Event;
import com.example.eventservice.model.Team;
import com.example.eventservice.repository.EventApplicationRepository;
import com.example.eventservice.repository.EventRepository;
import com.example.eventservice.repository.TeamMemberRepository;
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
class TeamControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventApplicationRepository eventApplicationRepository;

    private static final UUID STUDENT_ID = UUID.randomUUID();
    private static final UUID LECTURER_ID = UUID.randomUUID();

    private Event testEvent;

    @BeforeEach
    void setUp() {
        teamMemberRepository.deleteAll();
        eventApplicationRepository.deleteAll();
        teamRepository.deleteAll();
        eventRepository.deleteAll();

        testEvent = eventRepository.save(Event.builder()
                .id(UUID.randomUUID())
                .title("Team Event")
                .description("An event for team tests")
                .startDate(OffsetDateTime.now().plusDays(7))
                .endDate(OffsetDateTime.now().plusDays(9))
                .format(EventFormat.OFFLINE)
                .status(EventStatus.REGISTRATION_OPEN)
                .createdBy(LECTURER_ID)
                .build());
    }

    private Team createTeamInDb(String name) {
        return teamRepository.save(Team.builder()
                .event(testEvent)
                .name(name)
                .createdBy(STUDENT_ID)
                .ideaDescription("Some idea")
                .build());
    }

    @Test
    void getAllTeams_ReturnsList() throws Exception {
        createTeamInDb("Team Alpha");
        createTeamInDb("Team Beta");

        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/teams"),
                        STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getTeamById_Returns200() throws Exception {
        Team team = createTeamInDb("Team Gamma");

        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/teams/" + team.getId()),
                        STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Team Gamma"))
                .andExpect(jsonPath("$.eventId").value(testEvent.getId().toString()));
    }

    @Test
    void createTeam_AsStudent_Returns201() throws Exception {
        CreateTeamDto dto = CreateTeamDto.builder()
                .eventId(testEvent.getId())
                .name("New Team")
                .build();

        mockMvc.perform(TestSecurityHelper.withStudent(
                        post("/api/v1/teams")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)),
                        STUDENT_ID))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Team"))
                .andExpect(jsonPath("$.eventId").value(testEvent.getId().toString()));

        assertThat(teamRepository.count()).isEqualTo(1);
    }

    @Test
    void updateTeam_Returns200() throws Exception {
        Team team = createTeamInDb("Old Name");

        UpdateTeamDto updateDto = UpdateTeamDto.builder()
                .name("New Name")
                .build();

        mockMvc.perform(TestSecurityHelper.withStudent(
                        put("/api/v1/teams/" + team.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDto)),
                        STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));

        Team updated = teamRepository.findById(team.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("New Name");
    }

    @Test
    void deleteTeam_Returns204() throws Exception {
        Team team = createTeamInDb("To Delete");

        mockMvc.perform(TestSecurityHelper.withStudent(
                        delete("/api/v1/teams/" + team.getId()),
                        STUDENT_ID))
                .andExpect(status().isNoContent());

        assertThat(teamRepository.findById(team.getId())).isEmpty();
    }

    @Test
    void unauthenticatedRequest_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/teams"))
                .andExpect(status().isForbidden());
    }
}
