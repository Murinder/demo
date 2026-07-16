package com.example.documentservice.service;

import com.example.documentservice.dto.PlaceholderDto;
import com.example.documentservice.model.entity.Placeholder;
import com.example.documentservice.model.enums.PlaceholderType;
import com.example.documentservice.repository.PlaceholderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceholderServiceTest {

    @Mock
    private PlaceholderRepository placeholderRepository;

    @InjectMocks
    private PlaceholderService placeholderService;

    @Test
    void getPlaceholdersByTemplateId_shouldReturnPlaceholders() {
        UUID templateId = UUID.randomUUID();

        Placeholder p1 = Placeholder.builder()
                .id(Placeholder.PlaceholderId.builder()
                        .templateId(templateId)
                        .placeholder("{{studentName}}")
                        .build())
                .description("Full name of the student")
                .dataType("STRING")
                .placeholderType(PlaceholderType.USER)
                .exampleValue("John Doe")
                .isRequired(true)
                .build();

        Placeholder p2 = Placeholder.builder()
                .id(Placeholder.PlaceholderId.builder()
                        .templateId(templateId)
                        .placeholder("{{graduationDate}}")
                        .build())
                .description("Date of graduation")
                .dataType("DATE")
                .placeholderType(PlaceholderType.ACADEMIC)
                .exampleValue("2026-06-15")
                .isRequired(true)
                .build();

        when(placeholderRepository.findByIdTemplateId(templateId)).thenReturn(List.of(p1, p2));

        List<PlaceholderDto> results = placeholderService.getPlaceholdersByTemplateId(templateId);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getPlaceholder()).isEqualTo("{{studentName}}");
        assertThat(results.get(0).getPlaceholderType()).isEqualTo(PlaceholderType.USER);
        assertThat(results.get(0).getIsRequired()).isTrue();
        assertThat(results.get(1).getPlaceholder()).isEqualTo("{{graduationDate}}");
        assertThat(results.get(1).getPlaceholderType()).isEqualTo(PlaceholderType.ACADEMIC);
        verify(placeholderRepository).findByIdTemplateId(templateId);
    }

    @Test
    void getPlaceholdersByTemplateId_whenNoPlaceholders_shouldReturnEmptyList() {
        UUID templateId = UUID.randomUUID();
        when(placeholderRepository.findByIdTemplateId(templateId)).thenReturn(List.of());

        List<PlaceholderDto> results = placeholderService.getPlaceholdersByTemplateId(templateId);

        assertThat(results).isEmpty();
    }

    @Test
    void getPlaceholdersByTemplateId_shouldMapAllFieldsCorrectly() {
        UUID templateId = UUID.randomUUID();

        Placeholder placeholder = Placeholder.builder()
                .id(Placeholder.PlaceholderId.builder()
                        .templateId(templateId)
                        .placeholder("{{projectTitle}}")
                        .build())
                .description("Title of the project")
                .dataType("STRING")
                .placeholderType(PlaceholderType.PROJECT)
                .exampleValue("My Diploma Project")
                .isRequired(false)
                .build();

        when(placeholderRepository.findByIdTemplateId(templateId)).thenReturn(List.of(placeholder));

        List<PlaceholderDto> results = placeholderService.getPlaceholdersByTemplateId(templateId);

        assertThat(results).hasSize(1);
        PlaceholderDto dto = results.get(0);
        assertThat(dto.getTemplateId()).isEqualTo(templateId);
        assertThat(dto.getPlaceholder()).isEqualTo("{{projectTitle}}");
        assertThat(dto.getDescription()).isEqualTo("Title of the project");
        assertThat(dto.getDataType()).isEqualTo("STRING");
        assertThat(dto.getPlaceholderType()).isEqualTo(PlaceholderType.PROJECT);
        assertThat(dto.getExampleValue()).isEqualTo("My Diploma Project");
        assertThat(dto.getIsRequired()).isFalse();
    }
}
