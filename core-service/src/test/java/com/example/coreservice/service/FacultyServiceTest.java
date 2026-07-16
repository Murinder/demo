package com.example.coreservice.service;

import com.example.coreservice.dto.FacultyDto;
import com.example.coreservice.model.entity.Faculty;
import com.example.coreservice.repository.FacultyRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import com.example.sharedlib.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FacultyServiceTest {

    @Mock
    private FacultyRepository facultyRepository;

    @InjectMocks
    private FacultyService facultyService;

    @Test
    void getAllFaculties_ReturnsList() {
        Faculty faculty = Faculty.builder().id(UUID.randomUUID()).name("Engineering").code("ENG").build();
        when(facultyRepository.findAll()).thenReturn(List.of(faculty));

        List<FacultyDto> result = facultyService.getAllFaculties();

        assertEquals(1, result.size());
        assertEquals("Engineering", result.get(0).getName());
    }

    @Test
    void getFacultyById_WhenExists_ReturnsFaculty() {
        UUID id = UUID.randomUUID();
        Faculty faculty = Faculty.builder().id(id).name("Science").code("SCI").build();
        when(facultyRepository.findById(id)).thenReturn(Optional.of(faculty));

        FacultyDto result = facultyService.getFacultyById(id);

        assertEquals("Science", result.getName());
    }

    @Test
    void getFacultyById_WhenNotExists_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(facultyRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> facultyService.getFacultyById(id));
    }

    @Test
    void createFaculty_WithUniqueCode_CreatesFaculty() {
        FacultyDto dto = FacultyDto.builder().name("Arts").code("ART").build();
        when(facultyRepository.existsByCode("ART")).thenReturn(false);
        when(facultyRepository.save(any(Faculty.class))).thenAnswer(invocation -> {
            Faculty f = invocation.getArgument(0);
            f.setId(UUID.randomUUID());
            return f;
        });

        FacultyDto result = facultyService.createFaculty(dto);

        assertNotNull(result);
        assertEquals("Arts", result.getName());
        verify(facultyRepository).save(any(Faculty.class));
    }

    @Test
    void createFaculty_WithDuplicateCode_ThrowsException() {
        FacultyDto dto = FacultyDto.builder().name("Arts").code("ART").build();
        when(facultyRepository.existsByCode("ART")).thenReturn(true);

        assertThrows(ValidationException.class, () -> facultyService.createFaculty(dto));
    }
}
