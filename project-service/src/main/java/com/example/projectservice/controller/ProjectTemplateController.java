package com.example.projectservice.controller;

import com.example.projectservice.dto.ProjectTemplateDto;
import com.example.projectservice.service.ProjectTemplateService;
import com.example.sharedlib.security.AuthenticatedOnly;
import com.example.sharedlib.security.LecturerOrAbove;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/project-templates")
@RequiredArgsConstructor
@AuthenticatedOnly
public class ProjectTemplateController {

    private final ProjectTemplateService projectTemplateService;

    @LecturerOrAbove
    @PostMapping
    public ProjectTemplateDto createTemplate(@RequestBody ProjectTemplateDto templateDto) {
        return projectTemplateService.createTemplate(templateDto);
    }

    @GetMapping("/{templateId}")
    public ProjectTemplateDto getTemplateById(@PathVariable UUID templateId) {
        return projectTemplateService.getTemplateById(templateId);
    }

    @GetMapping
    public List<ProjectTemplateDto> getAllTemplates() {
        return projectTemplateService.getAllTemplates();
    }
}