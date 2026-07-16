package com.example.coreservice.service;

import com.example.coreservice.model.dto.UserLinkDto;
import com.example.coreservice.model.entity.UserLink;
import com.example.coreservice.model.entity.UserLinkId;
import com.example.coreservice.model.enums.LinkType;
import com.example.coreservice.repository.UserLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserLinkService {

    private final UserLinkRepository userLinkRepository;

    public List<UserLinkDto> getAllUserLinks() {
        return userLinkRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<UserLinkDto> getUserLinksByUserId(UUID userId) {
        return userLinkRepository.findByIdUserId(userId).stream().map(this::toDto).toList();
    }

    public UserLinkDto getUserLinkById(UUID userId, LinkType linkType) {
        UserLinkId id = new UserLinkId(userId, linkType);
        return userLinkRepository.findById(id).map(this::toDto).orElse(null);
    }

    public UserLinkDto createUserLink(UserLinkDto userLinkDto) {
        UserLink userLink = toEntity(userLinkDto);
        return toDto(userLinkRepository.save(userLink));
    }

    public UserLinkDto updateUserLink(UUID userId, LinkType linkType, UserLinkDto userLinkDto) {
        UserLinkId id = new UserLinkId(userId, linkType);
        if (!userLinkRepository.existsById(id)) {
            return null;
        }
        UserLink userLink = toEntity(userLinkDto);
        userLink.setId(id);
        return toDto(userLinkRepository.save(userLink));
    }

    public void deleteUserLink(UUID userId, LinkType linkType) {
        UserLinkId id = new UserLinkId(userId, linkType);
        userLinkRepository.deleteById(id);
    }

    private UserLinkDto toDto(UserLink userLink) {
        return new UserLinkDto(
                userLink.getId().getUserId(),
                userLink.getId().getLinkType(),
                userLink.getUrl()
        );
    }

    private UserLink toEntity(UserLinkDto userLinkDto) {
        return UserLink.builder()
                .id(new UserLinkId(userLinkDto.userId(), userLinkDto.linkType()))
                .url(userLinkDto.url())
                .build();
    }
}