package com.example.projectservice.integration;

import com.example.projectservice.client.UserServiceClient;
import com.example.projectservice.model.Project;
import com.example.projectservice.model.ProjectMember;
import com.example.projectservice.repository.ProjectMemberRepository;
import com.example.projectservice.repository.ProjectRepository;
import com.example.projectservice.repository.TaskRepository;
import com.example.sharedlib.dto.ProjectDto;
import com.example.sharedlib.enums.ProjectRole;
import com.example.sharedlib.enums.ProjectStatus;
import com.example.sharedlib.storage.FileStorageService;
import com.example.testsupport.BaseIntegrationTest;
import com.example.testsupport.security.TestSecurityHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ProjectControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private TaskRepository taskRepository;

    @MockBean
    private UserServiceClient userServiceClient;

    @MockBean
    private FileStorageService fileStorageService;

    private static final UUID LECTURER_ID = UUID.randomUUID();
    private static final UUID STUDENT_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        projectMemberRepository.deleteAll();
        projectRepository.deleteAll();
    }

    private ProjectDto buildProjectDto() {
        return ProjectDto.builder()
                .title("Test Project")
                .description("A test project description")
                .status(ProjectStatus.ACTIVE)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(3))
                .createdBy(LECTURER_ID)
                .departmentId(UUID.randomUUID())
                .build();
    }

    @Test
    void createProject_AsLecturer_Returns201() throws Exception {
        ProjectDto dto = buildProjectDto();

        mockMvc.perform(TestSecurityHelper.withLecturer(
                        post("/api/v1/projects")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)),
                        LECTURER_ID))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("PROJECT_CREATED"))
                .andExpect(jsonPath("$.data.title").value("Test Project"));

        assertThat(projectRepository.count()).isEqualTo(1);
    }

    @Test
    void createProject_AsStudent_Returns403() throws Exception {
        ProjectDto dto = buildProjectDto();

        mockMvc.perform(TestSecurityHelper.withStudent(
                        post("/api/v1/projects")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)),
                        STUDENT_ID))
                .andExpect(status().isForbidden());

        assertThat(projectRepository.count()).isEqualTo(0);
    }

    @Test
    void getProject_AsMember_Returns200() throws Exception {
        // Create project directly in DB
        Project project = projectRepository.save(Project.builder()
                .id(UUID.randomUUID())
                .title("Existing Project")
                .description("desc")
                .status(ProjectStatus.ACTIVE)
                .createdBy(LECTURER_ID)
                .build());

        // Add the requesting user as a member
        projectMemberRepository.save(ProjectMember.builder()
                .id(new ProjectMember.ProjectMemberId(project.getId(), STUDENT_ID))
                .project(project)
                .role(ProjectRole.MEMBER)
                .build());

        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/projects/" + project.getId()),
                        STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Existing Project"));
    }

    @Test
    void getUserProjects_ReturnsProjectsForUser() throws Exception {
        // Create project and add user as member
        Project project = projectRepository.save(Project.builder()
                .id(UUID.randomUUID())
                .title("User Project")
                .description("desc")
                .status(ProjectStatus.ACTIVE)
                .createdBy(LECTURER_ID)
                .build());

        projectMemberRepository.save(ProjectMember.builder()
                .id(new ProjectMember.ProjectMemberId(project.getId(), STUDENT_ID))
                .project(project)
                .role(ProjectRole.MEMBER)
                .build());

        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/projects/user/" + STUDENT_ID),
                        STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].title").value("User Project"));
    }

    @Test
    void updateProject_AsLeader_Returns200() throws Exception {
        Project project = projectRepository.save(Project.builder()
                .id(UUID.randomUUID())
                .title("Original Title")
                .description("desc")
                .status(ProjectStatus.ACTIVE)
                .createdBy(LECTURER_ID)
                .build());

        projectMemberRepository.save(ProjectMember.builder()
                .id(new ProjectMember.ProjectMemberId(project.getId(), LECTURER_ID))
                .project(project)
                .role(ProjectRole.LEADER)
                .build());

        ProjectDto updateDto = ProjectDto.builder()
                .title("Updated Title")
                .build();

        mockMvc.perform(TestSecurityHelper.withLecturer(
                        put("/api/v1/projects/" + project.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDto)),
                        LECTURER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Updated Title"));
    }

    @Test
    void changeProjectStatus_AsLeader_Returns200() throws Exception {
        Project project = projectRepository.save(Project.builder()
                .id(UUID.randomUUID())
                .title("Status Project")
                .description("desc")
                .status(ProjectStatus.ACTIVE)
                .createdBy(LECTURER_ID)
                .build());

        projectMemberRepository.save(ProjectMember.builder()
                .id(new ProjectMember.ProjectMemberId(project.getId(), LECTURER_ID))
                .project(project)
                .role(ProjectRole.LEADER)
                .build());

        mockMvc.perform(TestSecurityHelper.withLecturer(
                        patch("/api/v1/projects/" + project.getId() + "/status")
                                .param("status", "COMPLETED"),
                        LECTURER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));

        Project updated = projectRepository.findById(project.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(ProjectStatus.COMPLETED);
    }

    @Test
    void deleteProject_AsLeader_Returns200() throws Exception {
        Project project = projectRepository.save(Project.builder()
                .id(UUID.randomUUID())
                .title("To Delete")
                .description("desc")
                .status(ProjectStatus.ACTIVE)
                .createdBy(LECTURER_ID)
                .build());

        projectMemberRepository.save(ProjectMember.builder()
                .id(new ProjectMember.ProjectMemberId(project.getId(), LECTURER_ID))
                .project(project)
                .role(ProjectRole.LEADER)
                .build());

        mockMvc.perform(TestSecurityHelper.withLecturer(
                        delete("/api/v1/projects/" + project.getId()),
                        LECTURER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("PROJECT_DELETED"));

        assertThat(projectRepository.findById(project.getId())).isEmpty();
    }

    @Test
    void unauthenticatedRequest_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/projects/user/" + UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }
}
