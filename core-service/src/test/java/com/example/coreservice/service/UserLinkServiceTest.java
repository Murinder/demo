package com.example.coreservice.service;

import com.example.coreservice.model.dto.UserLinkDto;
import com.example.coreservice.model.entity.UserLink;
import com.example.coreservice.model.entity.UserLinkId;
import com.example.coreservice.model.enums.LinkType;
import com.example.coreservice.repository.UserLinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserLinkServiceTest {

    @Mock
    private UserLinkRepository userLinkRepository;

    @InjectMocks
    private UserLinkService userLinkService;

    private UUID userId;
    private LinkType linkType;
    private UserLinkId compositeId;
    private UserLink testUserLink;
    private UserLinkDto testUserLinkDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        linkType = LinkType.GITHUB;
        compositeId = new UserLinkId(userId, linkType);

        testUserLink = UserLink.builder()
                .id(compositeId)
                .url("https://github.com/testuser")
                .build();

        testUserLinkDto = new UserLinkDto(userId, linkType, "https://github.com/testuser");
    }

    @Test
    void getAllUserLinks_ReturnsList() {
        when(userLinkRepository.findAll()).thenReturn(List.of(testUserLink));

        List<UserLinkDto> result = userLinkService.getAllUserLinks();

        assertEquals(1, result.size());
        assertEquals(LinkType.GITHUB, result.get(0).linkType());
        assertEquals("https://github.com/testuser", result.get(0).url());
        verify(userLinkRepository, times(1)).findAll();
    }

    @Test
    void getAllUserLinks_ReturnsEmptyList() {
        when(userLinkRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserLinkDto> result = userLinkService.getAllUserLinks();

        assertTrue(result.isEmpty());
    }

    @Test
    void getUserLinkById_WhenExists_ReturnsLink() {
        when(userLinkRepository.findById(compositeId)).thenReturn(Optional.of(testUserLink));

        UserLinkDto result = userLinkService.getUserLinkById(userId, linkType);

        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals(LinkType.GITHUB, result.linkType());
        assertEquals("https://github.com/testuser", result.url());
    }

    @Test
    void getUserLinkById_WhenNotExists_ReturnsNull() {
        UUID id = UUID.randomUUID();
        UserLinkId nonExistentId = new UserLinkId(id, LinkType.LINKEDIN);
        when(userLinkRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        UserLinkDto result = userLinkService.getUserLinkById(id, LinkType.LINKEDIN);

        assertNull(result);
    }

    @Test
    void createUserLink_Success() {
        when(userLinkRepository.save(any(UserLink.class))).thenReturn(testUserLink);

        UserLinkDto result = userLinkService.createUserLink(testUserLinkDto);

        assertNotNull(result);
        assertEquals(LinkType.GITHUB, result.linkType());
        assertEquals("https://github.com/testuser", result.url());
        verify(userLinkRepository, times(1)).save(any(UserLink.class));
    }

    @Test
    void updateUserLink_WhenExists_ReturnsUpdated() {
        when(userLinkRepository.existsById(compositeId)).thenReturn(true);
        when(userLinkRepository.save(any(UserLink.class))).thenReturn(testUserLink);

        UserLinkDto result = userLinkService.updateUserLink(userId, linkType, testUserLinkDto);

        assertNotNull(result);
        assertEquals("https://github.com/testuser", result.url());
        verify(userLinkRepository, times(1)).save(any(UserLink.class));
    }

    @Test
    void updateUserLink_WhenNotExists_ReturnsNull() {
        UUID id = UUID.randomUUID();
        UserLinkId nonExistentId = new UserLinkId(id, LinkType.WEBSITE);
        when(userLinkRepository.existsById(nonExistentId)).thenReturn(false);

        UserLinkDto result = userLinkService.updateUserLink(id, LinkType.WEBSITE, testUserLinkDto);

        assertNull(result);
        verify(userLinkRepository, never()).save(any(UserLink.class));
    }

    @Test
    void deleteUserLink_CallsRepository() {
        doNothing().when(userLinkRepository).deleteById(compositeId);

        userLinkService.deleteUserLink(userId, linkType);

        verify(userLinkRepository, times(1)).deleteById(compositeId);
    }
}
