package com.example.coreservice.service;

import com.example.coreservice.model.dto.UserSkillDto;
import com.example.coreservice.model.entity.UserSkill;
import com.example.coreservice.model.entity.UserSkillId;
import com.example.coreservice.repository.UserSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSkillService {

    private final UserSkillRepository userSkillRepository;

    public List<UserSkillDto> getAllUserSkills() {
        return userSkillRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<UserSkillDto> getUserSkillsByUserId(UUID userId) {
        return userSkillRepository.findByIdUserId(userId).stream().map(this::toDto).toList();
    }

    public UserSkillDto getUserSkillById(UUID userId, String skillName) {
        UserSkillId id = new UserSkillId(userId, skillName);
        return userSkillRepository.findById(id).map(this::toDto).orElse(null);
    }

    public UserSkillDto createUserSkill(UserSkillDto userSkillDto) {
        UserSkill userSkill = toEntity(userSkillDto);
        return toDto(userSkillRepository.save(userSkill));
    }

    public UserSkillDto updateUserSkill(UUID userId, String skillName, UserSkillDto userSkillDto) {
        UserSkillId id = new UserSkillId(userId, skillName);
        if (!userSkillRepository.existsById(id)) {
            return null;
        }
        UserSkill userSkill = toEntity(userSkillDto);
        userSkill.setId(id);
        return toDto(userSkillRepository.save(userSkill));
    }

    public void deleteUserSkill(UUID userId, String skillName) {
        UserSkillId id = new UserSkillId(userId, skillName);
        userSkillRepository.deleteById(id);
    }

    private UserSkillDto toDto(UserSkill userSkill) {
        return new UserSkillDto(
                userSkill.getId().getUserId(),
                userSkill.getId().getSkillName(),
                userSkill.getLevel(),
                userSkill.isVerified()
        );
    }

    private UserSkill toEntity(UserSkillDto userSkillDto) {
        return UserSkill.builder()
                .id(new UserSkillId(userSkillDto.userId(), userSkillDto.skillName()))
                .level(userSkillDto.level())
                .verified(userSkillDto.verified())
                .build();
    }
}