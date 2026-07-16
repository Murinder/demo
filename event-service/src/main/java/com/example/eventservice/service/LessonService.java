package com.example.eventservice.service;

import com.example.eventservice.dto.CreateLessonDto;
import com.example.eventservice.dto.LessonDto;
import com.example.eventservice.model.Lesson;
import com.example.eventservice.model.enums.LessonType;
import com.example.eventservice.repository.LessonRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LessonService {

    private final LessonRepository lessonRepository;

    @Transactional(readOnly = true)
    public List<LessonDto> getLessonsByUser(UUID userId, Integer semester) {
        List<Lesson> lessons = semester != null
                ? lessonRepository.findByUserIdAndSemester(userId, semester)
                : lessonRepository.findByUserId(userId);
        return lessons.stream().map(this::toDto).toList();
    }

    public LessonDto createLesson(CreateLessonDto dto) {
        LocalDate lessonDate = dto.getLessonDate() != null
                ? LocalDate.parse(dto.getLessonDate())
                : null;

        DayOfWeek dayOfWeek = lessonDate != null
                ? lessonDate.getDayOfWeek()
                : DayOfWeek.valueOf(dto.getDayOfWeek());

        Lesson lesson = Lesson.builder()
                .userId(dto.getUserId())
                .dayOfWeek(dayOfWeek)
                .timeSlot(dto.getTimeSlot())
                .subject(dto.getSubject())
                .lessonType(LessonType.valueOf(dto.getLessonType()))
                .groupName(dto.getGroupName())
                .room(dto.getRoom())
                .semester(dto.getSemester())
                .lessonDate(lessonDate)
                .build();
        return toDto(lessonRepository.save(lesson));
    }

    public LessonDto updateLesson(UUID id, CreateLessonDto dto) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        LocalDate lessonDate = dto.getLessonDate() != null
                ? LocalDate.parse(dto.getLessonDate())
                : null;

        DayOfWeek dayOfWeek = lessonDate != null
                ? lessonDate.getDayOfWeek()
                : DayOfWeek.valueOf(dto.getDayOfWeek());

        lesson.setDayOfWeek(dayOfWeek);
        lesson.setTimeSlot(dto.getTimeSlot());
        lesson.setSubject(dto.getSubject());
        lesson.setLessonType(LessonType.valueOf(dto.getLessonType()));
        lesson.setGroupName(dto.getGroupName());
        lesson.setRoom(dto.getRoom());
        lesson.setSemester(dto.getSemester());
        lesson.setLessonDate(lessonDate);

        return toDto(lessonRepository.save(lesson));
    }

    public void deleteLesson(UUID id) {
        if (!lessonRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lesson not found");
        }
        lessonRepository.deleteById(id);
    }

    private LessonDto toDto(Lesson l) {
        return LessonDto.builder()
                .id(l.getId())
                .userId(l.getUserId())
                .dayOfWeek(l.getDayOfWeek().name())
                .timeSlot(l.getTimeSlot())
                .subject(l.getSubject())
                .lessonType(l.getLessonType().name())
                .groupName(l.getGroupName())
                .room(l.getRoom())
                .semester(l.getSemester())
                .lessonDate(l.getLessonDate() != null ? l.getLessonDate().toString() : null)
                .build();
    }
}
