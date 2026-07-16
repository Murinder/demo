package com.example.coreservice.service;

import com.example.coreservice.dto.ApplicationCommentDto;
import com.example.coreservice.dto.ApplicationViewDto;
import com.example.coreservice.dto.CreateApplicationDto;
import com.example.coreservice.model.entity.Application;
import com.example.coreservice.model.entity.ApplicationComment;
import com.example.coreservice.model.entity.User;
import com.example.coreservice.model.enums.ApplicationKind;
import com.example.coreservice.model.enums.ApplicationPriority;
import com.example.coreservice.model.enums.ApplicationStatus;
import com.example.coreservice.repository.ApplicationCommentRepository;
import com.example.coreservice.repository.ApplicationRepository;
import com.example.coreservice.repository.UserRepository;
import jakarta.persistence.EntityManager;
import com.example.sharedlib.config.RabbitMqAutoConfiguration;
import com.example.sharedlib.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationCommentRepository commentRepository;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;
    private final EntityManager entityManager;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private static final Set<ApplicationKind> ADMIN_REQUIRED_KINDS = Set.of(
            ApplicationKind.RESOURCE_REQUEST,
            ApplicationKind.EQUIPMENT_REQUEST,
            ApplicationKind.ROOM_REQUEST
    );

    private static final Set<ApplicationStatus> ACTIVE_STATUSES = Set.of(
            ApplicationStatus.PENDING,
            ApplicationStatus.REVISION,
            ApplicationStatus.ADMIN_REVIEW,
            ApplicationStatus.IN_PROGRESS
    );

    @Transactional(readOnly = true)
    public List<ApplicationViewDto> getByLecturer(UUID lecturerId) {
        return applicationRepository.findByLecturerIdOrderBySubmittedAtDesc(lecturerId)
                .stream()
                .map(this::mapToViewDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ApplicationViewDto> getByStudent(UUID studentId) {
        return applicationRepository.findByStudentIdOrderBySubmittedAtDesc(studentId)
                .stream()
                .map(this::mapToViewDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ApplicationViewDto> getByAdmin() {
        List<ApplicationKind> adminKinds = List.of(
                ApplicationKind.RESOURCE_REQUEST,
                ApplicationKind.EQUIPMENT_REQUEST,
                ApplicationKind.ROOM_REQUEST
        );
        List<ApplicationStatus> adminStatuses = List.of(
                ApplicationStatus.ADMIN_REVIEW,
                ApplicationStatus.IN_PROGRESS,
                ApplicationStatus.COMPLETED,
                ApplicationStatus.REJECTED
        );
        return applicationRepository.findByKindInAndStatusInOrderBySubmittedAtDesc(adminKinds, adminStatuses)
                .stream()
                .map(this::mapToViewDto)
                .collect(Collectors.toList());
    }

    public ApplicationViewDto create(UUID studentId, CreateApplicationDto dto) {
        ApplicationKind kind = ApplicationKind.valueOf(dto.getKind());

        Application app = Application.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .studentId(studentId)
                .lecturerId(dto.getLecturerId())
                .kind(kind)
                .priority(dto.getPriority() != null
                        ? ApplicationPriority.valueOf(dto.getPriority())
                        : ApplicationPriority.MEDIUM)
                .category(dto.getCategory())
                .duration(dto.getDuration())
                .teamSize(dto.getTeamSize())
                .build();

        Application saved = applicationRepository.save(app);

        // Auto-generate title for new application kinds
        if (saved.getTitle() == null || saved.getTitle().isBlank()) {
            applicationRepository.flush();
            entityManager.refresh(saved);
            Integer appNum = saved.getApplicationNumber();
            saved.setTitle("Заявка №" + (appNum != null ? appNum : saved.getId().toString().substring(0, 8)));
            saved = applicationRepository.save(saved);
        }

        log.info("Application created: {} by student {}", saved.getId(), studentId);

        // Notify the lecturer about the new application
        User student = userRepository.findById(studentId).orElse(null);
        String studentName = student != null
                ? (student.getFirstName() + " " + student.getLastName()).trim()
                : "Студент";
        notifyUser(dto.getLecturerId(), "Новая заявка",
                studentName + " подал(а) заявку: " + saved.getTitle());

        return mapToViewDto(saved);
    }

    public ApplicationViewDto updateStatus(UUID applicationId, String status, String comment) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        ApplicationStatus newStatus = ApplicationStatus.valueOf(status);
        ApplicationStatus oldStatus = app.getStatus();

        switch (newStatus) {
            case REVISION -> {
                // Teacher requests more info — comment required
                if (comment != null && !comment.isBlank()) {
                    createComment(applicationId, app.getLecturerId(), "LECTURER", comment);
                }
                app.setStatus(ApplicationStatus.REVISION);
            }
            case APPROVED -> {
                // Teacher approves
                if (ADMIN_REQUIRED_KINDS.contains(app.getKind())) {
                    // Forward to department head for second stage
                    app.setStatus(ApplicationStatus.ADMIN_REVIEW);
                    notifyUser(app.getStudentId(), "Заявка одобрена преподавателем",
                            "Ваша заявка \"" + app.getTitle() + "\" одобрена преподавателем и направлена на подтверждение заведующему кафедрой");
                } else {
                    // OTHER kind — teacher approval is final
                    app.setStatus(ApplicationStatus.COMPLETED);
                    notifyUser(app.getStudentId(), "Заявка исполнена",
                            "Ваша заявка \"" + app.getTitle() + "\" была одобрена");
                }
                if (comment != null && !comment.isBlank()) {
                    createComment(applicationId, app.getLecturerId(), "LECTURER", comment);
                }
            }
            case REJECTED -> {
                // Teacher or admin rejects — comment required
                if (comment != null && !comment.isBlank()) {
                    UUID authorId = oldStatus == ApplicationStatus.ADMIN_REVIEW || oldStatus == ApplicationStatus.IN_PROGRESS
                            ? (app.getAdminId() != null ? app.getAdminId() : app.getLecturerId())
                            : app.getLecturerId();
                    String authorRole = oldStatus == ApplicationStatus.ADMIN_REVIEW || oldStatus == ApplicationStatus.IN_PROGRESS
                            ? "DEPARTMENT_HEAD" : "LECTURER";
                    createComment(applicationId, authorId, authorRole, comment);
                }
                app.setStatus(ApplicationStatus.REJECTED);
                notifyUser(app.getStudentId(), "Заявка отклонена",
                        "Ваша заявка \"" + app.getTitle() + "\" была отклонена");
            }
            case WITHDRAWN -> {
                // Student withdraws
                app.setStatus(ApplicationStatus.WITHDRAWN);
                notifyUser(app.getLecturerId(), "Заявка отозвана",
                        "Заявка \"" + app.getTitle() + "\" была отозвана студентом");
            }
            case IN_PROGRESS -> {
                // Admin accepts — start fulfillment
                app.setStatus(ApplicationStatus.IN_PROGRESS);
                notifyUser(app.getStudentId(), "Заявка принята к исполнению",
                        "Ваша заявка \"" + app.getTitle() + "\" принята к исполнению");
            }
            case COMPLETED -> {
                // Admin marks as fulfilled
                app.setStatus(ApplicationStatus.COMPLETED);
                notifyUser(app.getStudentId(), "Заявка исполнена",
                        "Ваша заявка \"" + app.getTitle() + "\" исполнена");
            }
            default -> {
                app.setStatus(newStatus);
                if (comment != null && !comment.isBlank()) {
                    app.setTeacherReply(comment);
                }
            }
        }

        app.setUpdatedAt(OffsetDateTime.now());
        Application saved = applicationRepository.save(app);
        log.info("Application {} status changed from {} to {}", applicationId, oldStatus, saved.getStatus());

        return mapToViewDto(saved);
    }

    public ApplicationViewDto addComment(UUID applicationId, UUID authorId, String content) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        User author = userRepository.findById(authorId).orElse(null);
        String authorRole = author != null ? author.getRole().name() : "STUDENT";

        createComment(applicationId, authorId, authorRole, content);

        // If student comments while status is REVISION, move back to PENDING
        if (app.getStatus() == ApplicationStatus.REVISION && app.getStudentId().equals(authorId)) {
            app.setStatus(ApplicationStatus.PENDING);
            app.setUpdatedAt(OffsetDateTime.now());
            applicationRepository.save(app);

            notifyUser(app.getLecturerId(), "Ответ на заявку",
                    "Студент дополнил заявку \"" + app.getTitle() + "\"");
        }

        return mapToViewDto(app);
    }

    public ApplicationViewDto withdraw(UUID applicationId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!ACTIVE_STATUSES.contains(app.getStatus())) {
            throw new IllegalStateException("Cannot withdraw application in status " + app.getStatus());
        }

        app.setStatus(ApplicationStatus.WITHDRAWN);
        app.setUpdatedAt(OffsetDateTime.now());
        Application saved = applicationRepository.save(app);

        notifyUser(app.getLecturerId(), "Заявка отозвана",
                "Заявка \"" + app.getTitle() + "\" была отозвана студентом");

        return mapToViewDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ApplicationCommentDto> getComments(UUID applicationId) {
        return commentRepository.findByApplicationIdOrderByCreatedAtAsc(applicationId)
                .stream()
                .map(this::mapCommentToDto)
                .collect(Collectors.toList());
    }

    private void createComment(UUID applicationId, UUID authorId, String authorRole, String content) {
        ApplicationComment comment = ApplicationComment.builder()
                .applicationId(applicationId)
                .authorId(authorId)
                .authorRole(authorRole)
                .content(content)
                .build();
        commentRepository.save(comment);
    }

    private void notifyUser(UUID userId, String title, String message) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMqAutoConfiguration.NOTIFICATION_EXCHANGE,
                    RabbitMqAutoConfiguration.NOTIFICATION_SEND_KEY,
                    Map.of(
                            "userId", userId.toString(),
                            "title", title,
                            "message", message,
                            "type", "SYSTEM"
                    )
            );
        } catch (Exception e) {
            log.warn("Failed to send notification: {}", e.getMessage());
        }
    }

    private ApplicationViewDto mapToViewDto(Application app) {
        User student = userRepository.findById(app.getStudentId()).orElse(null);

        ApplicationViewDto.StudentInfoDto studentInfo;
        if (student != null) {
            String firstName = student.getFirstName() != null ? student.getFirstName() : "";
            String lastName = student.getLastName() != null ? student.getLastName() : "";
            String fullName = (firstName + " " + lastName).trim();
            String initials = "";
            if (!firstName.isEmpty()) initials += firstName.charAt(0);
            if (!lastName.isEmpty()) initials += lastName.charAt(0);

            studentInfo = ApplicationViewDto.StudentInfoDto.builder()
                    .name(fullName)
                    .email(student.getEmail())
                    .phone(student.getPhone() != null ? student.getPhone() : "")
                    .group(student.getGroupName() != null ? student.getGroupName() : "")
                    .course(student.getCurrentSemester() != null
                            ? ((student.getCurrentSemester() + 1) / 2) + " курс"
                            : "")
                    .initials(initials.toUpperCase())
                    .build();
        } else {
            studentInfo = ApplicationViewDto.StudentInfoDto.builder()
                    .name("Неизвестный студент")
                    .email("")
                    .phone("")
                    .group("")
                    .course("")
                    .initials("??")
                    .build();
        }

        // Lecturer name
        User lecturer = userRepository.findById(app.getLecturerId()).orElse(null);
        String lecturerName = lecturer != null
                ? ((lecturer.getFirstName() != null ? lecturer.getFirstName() : "") + " "
                + (lecturer.getLastName() != null ? lecturer.getLastName() : "")).trim()
                : "";

        // Comments
        List<ApplicationCommentDto> comments = commentRepository
                .findByApplicationIdOrderByCreatedAtAsc(app.getId())
                .stream()
                .map(this::mapCommentToDto)
                .collect(Collectors.toList());

        return ApplicationViewDto.builder()
                .id(app.getId().toString())
                .title(app.getTitle())
                .description(app.getDescription())
                .submittedAt(app.getSubmittedAt().format(DATE_FMT))
                .status(app.getStatus().name())
                .priority(app.getPriority().name())
                .kind(app.getKind().name())
                .student(studentInfo)
                .category(app.getCategory())
                .duration(app.getDuration())
                .teamSize(app.getTeamSize())
                .teacherReply(app.getTeacherReply())
                .applicationNumber(app.getApplicationNumber())
                .adminId(app.getAdminId() != null ? app.getAdminId().toString() : null)
                .lecturerName(lecturerName)
                .comments(comments)
                .build();
    }

    private ApplicationCommentDto mapCommentToDto(ApplicationComment c) {
        User author = userRepository.findById(c.getAuthorId()).orElse(null);
        String authorName = author != null
                ? ((author.getFirstName() != null ? author.getFirstName() : "") + " "
                + (author.getLastName() != null ? author.getLastName() : "")).trim()
                : "Неизвестный";

        return ApplicationCommentDto.builder()
                .id(c.getId().toString())
                .applicationId(c.getApplicationId().toString())
                .authorId(c.getAuthorId().toString())
                .authorRole(c.getAuthorRole())
                .authorName(authorName)
                .content(c.getContent())
                .createdAt(c.getCreatedAt().format(DATETIME_FMT))
                .build();
    }
}
