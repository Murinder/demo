package com.example.coreservice.service;

import com.example.coreservice.dto.DepartmentDto;
import com.example.coreservice.model.entity.Department;
import com.example.coreservice.model.entity.Faculty;
import com.example.coreservice.repository.DepartmentRepository;
import com.example.coreservice.repository.FacultyRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import com.example.sharedlib.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
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
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private FacultyRepository facultyRepository;

    @InjectMocks
    private DepartmentService departmentService;

    private UUID departmentId;
    private UUID facultyId;
    private Faculty testFaculty;
    private Department testDepartment;

    @BeforeEach
    void setUp() {
        departmentId = UUID.randomUUID();
        facultyId = UUID.randomUUID();
        testFaculty = Faculty.builder()
                .id(facultyId)
                .name("Engineering")
                .code("ENG")
                .build();
        testDepartment = Department.builder()
                .id(departmentId)
                .name("Computer Science")
                .code("CS")
                .faculty(testFaculty)
                .description("CS department")
                .build();
    }

    @Test
    void getAllDepartments_ReturnsList() {
        when(departmentRepository.findAll()).thenReturn(List.of(testDepartment));

        List<DepartmentDto> result = departmentService.getAllDepartments();

        assertEquals(1, result.size());
        assertEquals("Computer Science", result.get(0).getName());
        assertEquals(facultyId, result.get(0).getFacultyId());
    }

    @Test
    void getDepartmentById_WhenExists_ReturnsDepartment() {
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(testDepartment));

        DepartmentDto result = departmentService.getDepartmentById(departmentId);

        assertNotNull(result);
        assertEquals("Computer Science", result.getName());
        assertEquals("CS", result.getCode());
    }

    @Test
    void getDepartmentById_WhenNotExists_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(departmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> departmentService.getDepartmentById(id));
    }

    @Test
    void getDepartmentsByFacultyId_ReturnsList() {
        when(departmentRepository.findByFacultyId(facultyId)).thenReturn(List.of(testDepartment));

        List<DepartmentDto> result = departmentService.getDepartmentsByFacultyId(facultyId);

        assertEquals(1, result.size());
        assertEquals("Computer Science", result.get(0).getName());
    }

    @Test
    void createDepartment_WithUniqueCode_CreatesDepartment() {
        DepartmentDto dto = DepartmentDto.builder()
                .name("Mathematics")
                .code("MATH")
                .facultyId(facultyId)
                .description("Math department")
                .build();

        when(departmentRepository.existsByCode("MATH")).thenReturn(false);
        when(facultyRepository.findById(facultyId)).thenReturn(Optional.of(testFaculty));
        when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> {
            Department d = invocation.getArgument(0);
            d.setId(UUID.randomUUID());
            return d;
        });

        DepartmentDto result = departmentService.createDepartment(dto);

        assertNotNull(result);
        assertEquals("Mathematics", result.getName());
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    void createDepartment_WithDuplicateCode_ThrowsException() {
        DepartmentDto dto = DepartmentDto.builder()
                .name("Mathematics")
                .code("CS")
                .facultyId(facultyId)
                .build();

        when(departmentRepository.existsByCode("CS")).thenReturn(true);

        assertThrows(ValidationException.class, () -> departmentService.createDepartment(dto));
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void createDepartment_WithNonExistentFaculty_ThrowsException() {
        UUID nonExistentFacultyId = UUID.randomUUID();
        DepartmentDto dto = DepartmentDto.builder()
                .name("Physics")
                .code("PHY")
                .facultyId(nonExistentFacultyId)
                .build();

        when(departmentRepository.existsByCode("PHY")).thenReturn(false);
        when(facultyRepository.findById(nonExistentFacultyId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> departmentService.createDepartment(dto));
    }

    @Test
    void updateDepartment_WhenExists_ReturnsUpdated() {
        DepartmentDto dto = DepartmentDto.builder()
                .name("Updated CS")
                .code("CS2")
                .facultyId(facultyId)
                .description("Updated description")
                .build();

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(testDepartment));
        when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DepartmentDto result = departmentService.updateDepartment(departmentId, dto);

        assertNotNull(result);
        assertEquals("Updated CS", result.getName());
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    void updateDepartment_WhenNotExists_ThrowsException() {
        UUID id = UUID.randomUUID();
        DepartmentDto dto = DepartmentDto.builder().name("Test").build();

        when(departmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> departmentService.updateDepartment(id, dto));
    }

    @Test
    void deleteDepartment_WhenExists_Deletes() {
        when(departmentRepository.existsById(departmentId)).thenReturn(true);
        doNothing().when(departmentRepository).deleteById(departmentId);

        departmentService.deleteDepartment(departmentId);

        verify(departmentRepository, times(1)).deleteById(departmentId);
    }

    @Test
    void deleteDepartment_WhenNotExists_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(departmentRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> departmentService.deleteDepartment(id));
    }
}
