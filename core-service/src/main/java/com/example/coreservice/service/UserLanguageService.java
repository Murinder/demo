package com.example.coreservice.service;

import com.example.coreservice.model.dto.UserLanguageDto;
import com.example.coreservice.model.entity.UserLanguage;
import com.example.coreservice.model.entity.UserLanguageId;
import com.example.coreservice.repository.UserLanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserLanguageService {

    private final UserLanguageRepository userLanguageRepository;

    public List<UserLanguageDto> getAllUserLanguages() {
        return userLanguageRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<UserLanguageDto> getUserLanguagesByUserId(UUID userId) {
        return userLanguageRepository.findByIdUserId(userId).stream().map(this::toDto).toList();
    }

    public UserLanguageDto getUserLanguageById(UUID userId, String language) {
        UserLanguageId id = new UserLanguageId(userId, language);
        return userLanguageRepository.findById(id).map(this::toDto).orElse(null);
    }

    public UserLanguageDto createUserLanguage(UserLanguageDto userLanguageDto) {
        UserLanguage userLanguage = toEntity(userLanguageDto);
        return toDto(userLanguageRepository.save(userLanguage));
    }

    public UserLanguageDto updateUserLanguage(UUID userId, String language, UserLanguageDto userLanguageDto) {
        UserLanguageId id = new UserLanguageId(userId, language);
        if (!userLanguageRepository.existsById(id)) {
            return null;
        }
        UserLanguage userLanguage = toEntity(userLanguageDto);
        userLanguage.setId(id);
        return toDto(userLanguageRepository.save(userLanguage));
    }

    public void deleteUserLanguage(UUID userId, String language) {
        UserLanguageId id = new UserLanguageId(userId, language);
        userLanguageRepository.deleteById(id);
    }

    private UserLanguageDto toDto(UserLanguage userLanguage) {
        return new UserLanguageDto(
                userLanguage.getId().getUserId(),
                userLanguage.getId().getLanguage(),
                userLanguage.getProficiency()
        );
    }

    private UserLanguage toEntity(UserLanguageDto userLanguageDto) {
        return UserLanguage.builder()
                .id(new UserLanguageId(userLanguageDto.userId(), userLanguageDto.language()))
                .proficiency(userLanguageDto.proficiency())
                .build();
    }
}