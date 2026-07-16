package com.example.documentservice.service;

import com.example.documentservice.dto.PlaceholderDto;
import com.example.documentservice.model.entity.Placeholder;
import com.example.documentservice.repository.PlaceholderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceholderService {

    private final PlaceholderRepository placeholderRepository;

    @Transactional(readOnly = true)
    public List<PlaceholderDto> getPlaceholdersByTemplateId(UUID templateId) {
        log.debug("Fetching placeholders for template: {}", templateId);
        return placeholderRepository.findByIdTemplateId(templateId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private PlaceholderDto toDto(Placeholder entity) {
        return PlaceholderDto.builder()
                .templateId(entity.getId().getTemplateId())
                .placeholder(entity.getId().getPlaceholder())
                .description(entity.getDescription())
                .dataType(entity.getDataType())
                .placeholderType(entity.getPlaceholderType())
                .exampleValue(entity.getExampleValue())
                .isRequired(entity.getIsRequired())
                .build();
    }
}
