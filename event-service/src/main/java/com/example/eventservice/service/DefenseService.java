package com.example.eventservice.service;

import com.example.eventservice.dto.CreateDefenseDto;
import com.example.eventservice.dto.DefenseDto;
import com.example.eventservice.model.Defense;
import com.example.eventservice.model.enums.DefenseStatus;
import com.example.eventservice.model.enums.DefenseType;
import com.example.eventservice.repository.DefenseRepository;
import com.example.sharedlib.config.RabbitMqAutoConfiguration;
import com.example.sharedlib.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DefenseService {

    private final DefenseRepository defenseRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional(readOnly = true)
    public List<DefenseDto> getDefensesBySupervisor(UUID supervisorId) {
        return defenseRepository.findBySupervisorIdOrderByDefenseDateAsc(supervisorId)
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<DefenseDto> getDefensesByStudent(UUID studentId) {
        return defenseRepository.findByStudentIdOrderByDefenseDateAsc(studentId)
                .stream().map(this::toDto).toList();
    }

    public DefenseDto createDefense(CreateDefenseDto dto) {
        Defense defense = Defense.builder()
                .studentId(dto.getStudentId())
                .studentName(dto.getStudentName())
                .supervisorId(dto.getSupervisorId())
                .defenseType(DefenseType.valueOf(dto.getDefenseType()))
                .projectTitle(dto.getProjectTitle())
                .defenseDate(dto.getDefenseDate())
                .defenseTime(dto.getDefenseTime())
                .room(dto.getRoom())
                .reviewersCount(dto.getReviewersCount())
                .build();
        Defense saved = defenseRepository.save(defense);

        // Notify the student about the scheduled defense
        rabbitTemplate.convertAndSend(
                RabbitMqAutoConfiguration.NOTIFICATION_EXCHANGE,
                RabbitMqAutoConfiguration.NOTIFICATION_SEND_KEY,
                Map.of(
                        "userId", saved.getStudentId().toString(),
                        "title", "Защита назначена",
                        "message", "Защита \"" + saved.getProjectTitle() + "\" назначена на "
                                + saved.getDefenseDate() + " в " + saved.getDefenseTime()
                                + ", аудитория " + saved.getRoom(),
                        "type", "EVENT_UPDATE"
                )
        );

        return toDto(saved);
    }

    public DefenseDto updateStatus(UUID id, String status, Integer grade) {
        Defense defense = defenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Defense not found"));
        defense.setStatus(DefenseStatus.valueOf(status));
        if (grade != null) defense.setGrade(grade);
        return toDto(defenseRepository.save(defense));
    }

    private DefenseDto toDto(Defense d) {
        return DefenseDto.builder()
                .id(d.getId())
                .studentId(d.getStudentId())
                .studentName(d.getStudentName())
                .supervisorId(d.getSupervisorId())
                .defenseType(d.getDefenseType().name())
                .status(d.getStatus().name())
                .projectTitle(d.getProjectTitle())
                .defenseDate(d.getDefenseDate())
                .defenseTime(d.getDefenseTime())
                .room(d.getRoom())
                .grade(d.getGrade())
                .reviewersCount(d.getReviewersCount())
                .build();
    }
}
