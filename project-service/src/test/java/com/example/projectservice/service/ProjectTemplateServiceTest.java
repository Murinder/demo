package com.example.projectservice.service;

import com.example.projectservice.dto.ProjectTemplateDto;
import com.example.projectservice.model.ProjectTemplate;
import com.example.projectservice.model.enums.TemplateType;
import com.example.projectservice.repository.ProjectTemplateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectTemplateServiceTest {

    @Mock
    private ProjectTemplateRepository projectTemplateRepository;

    @InjectMocks
    private ProjectTemplateService projectTemplateService;

    @Test
    void createTemplate_SavesAndReturnsDto() {
        UUID createdBy = UUID.randomUUID();
        ProjectTemplateDto inputDto = ProjectTemplateDto.builder()
                .name("Diploma Template")
                .description("Standard diploma project template")
                .templateType(TemplateType.DIPLOMA)
                .config("{\"stages\": [\"research\", \"implementation\", \"defense\"]}")
                .createdBy(createdBy)
                .build();

        when(projectTemplateRepository.save(any(ProjectTemplate.class))).thenAnswer(inv -> {
            ProjectTemplate t = inv.getArgument(0);
            t.setId(UUID.randomUUID());
            return t;
        });

        ProjectTemplateDto result = projectTemplateService.createTemplate(inputDto);

        assertNotNull(result);
        assertEquals("Diploma Template", result.getName());
        assertEquals(TemplateType.DIPLOMA, result.getTemplateType());
        assertEquals(createdBy, result.getCreatedBy());
        verify(projectTemplateRepository).save(any(ProjectTemplate.class));
    }

    @Test
    void getTemplateById_WhenFound_ReturnsDto() {
        UUID templateId = UUID.randomUUID();
        ProjectTemplate template = ProjectTemplate.builder()
                .id(templateId)
                .name("Research Template")
                .description("For research projects")
                .templateType(TemplateType.RESEARCH)
                .config("{}")
                .createdBy(UUID.randomUUID())
                .build();

        when(projectTemplateRepository.findById(templateId)).thenReturn(Optional.of(template));

        ProjectTemplateDto result = projectTemplateService.getTemplateById(templateId);

        assertNotNull(result);
        assertEquals(templateId, result.getId());
        assertEquals("Research Template", result.getName());
        assertEquals(TemplateType.RESEARCH, result.getTemplateType());
    }

    @Test
    void getTemplateById_WhenNotFound_ThrowsException() {
        UUID templateId = UUID.randomUUID();
        when(projectTemplateRepository.findById(templateId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> projectTemplateService.getTemplateById(templateId));
    }

    @Test
    void getAllTemplates_ReturnsList() {
        ProjectTemplate t1 = ProjectTemplate.builder()
                .id(UUID.randomUUID())
                .name("Template 1")
                .templateType(TemplateType.DIPLOMA)
                .createdBy(UUID.randomUUID())
                .build();
        ProjectTemplate t2 = ProjectTemplate.builder()
                .id(UUID.randomUUID())
                .name("Template 2")
                .templateType(TemplateType.HACKATHON)
                .createdBy(UUID.randomUUID())
                .build();

        when(projectTemplateRepository.findAll()).thenReturn(List.of(t1, t2));

        List<ProjectTemplateDto> result = projectTemplateService.getAllTemplates();

        assertEquals(2, result.size());
        assertEquals("Template 1", result.get(0).getName());
        assertEquals("Template 2", result.get(1).getName());
    }

    @Test
    void getAllTemplates_WhenEmpty_ReturnsEmptyList() {
        when(projectTemplateRepository.findAll()).thenReturn(List.of());

        List<ProjectTemplateDto> result = projectTemplateService.getAllTemplates();

        assertTrue(result.isEmpty());
    }
}
