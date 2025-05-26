package com.org.service;

import com.org.dto.request.DeveloperRequest;
import com.org.dto.response.DeveloperResponse;
import com.org.exception.DuplicateResourceException;
import com.org.exception.ResourceNotFoundException;
import com.org.mapper.DeveloperMapper;
import com.org.model.Developer;
import com.org.repository.DeveloperRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeveloperServiceUnitTest {

    @Mock
    private DeveloperRepository developerRepository;

    @Mock
    private DeveloperMapper developerMapper;

    @InjectMocks
    private DeveloperService developerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("CP01 - Registrar developer con nombre válido")
    void createDeveloper_validName_returnsCreated() {
        DeveloperRequest request = new DeveloperRequest("Henry");
        Developer entity = new Developer(1L, "Henry");
        DeveloperResponse response = new DeveloperResponse(1L, "Henry");

        when(developerRepository.existsByName("Henry")).thenReturn(false);
        when(developerMapper.toEntity(request)).thenReturn(entity);
        when(developerRepository.save(entity)).thenReturn(entity);
        when(developerMapper.toResponse(entity)).thenReturn(response);

        DeveloperResponse result = developerService.create(request);
        assertNotNull(result);
        assertEquals("Henry", result.name());
    }

    @Test
    @DisplayName("CP02 - Registrar developer con nombre duplicado")
    void createDeveloper_duplicateName_throwsException() {
        DeveloperRequest request = new DeveloperRequest("Henry");
        when(developerRepository.existsByName("Henry")).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> developerService.create(request));
    }

    @Test
    @DisplayName("CP03 - Registrar developer sin nombre válido")
    void createDeveloper_nullName_invalidEntity() {
        DeveloperRequest request = new DeveloperRequest(null);
        when(developerMapper.toEntity(request)).thenThrow(new IllegalArgumentException("El nombre no puede ser nulo"));
        assertThrows(IllegalArgumentException.class, () -> developerService.create(request));
    }

    @Test
    @DisplayName("CP04 - Listar developers con datos")
    void listDevelopers_withData_returnsList() {
        Developer d1 = new Developer(1L, "Henry");
        Developer d2 = new Developer(2L, "Maria");
        DeveloperResponse r1 = new DeveloperResponse(1L, "Henry");
        DeveloperResponse r2 = new DeveloperResponse(2L, "Maria");

        when(developerRepository.findAll()).thenReturn(Arrays.asList(d1, d2));
        when(developerMapper.toResponse(d1)).thenReturn(r1);
        when(developerMapper.toResponse(d2)).thenReturn(r2);

        List<DeveloperResponse> result = developerService.findAll();
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("CP05 - Listar developers sin datos")
    void listDevelopers_empty_returnsEmptyList() {
        when(developerRepository.findAll()).thenReturn(Collections.emptyList());
        List<DeveloperResponse> result = developerService.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("CP13 - Listar developers con paginación")
    void listDevelopers_withPagination_returnsPage() {
        Developer d1 = new Developer(1L, "Henry");
        DeveloperResponse r1 = new DeveloperResponse(1L, "Henry");

        Pageable pageable = PageRequest.of(0, 5);
        Page<Developer> page = new PageImpl<>(List.of(d1));

        when(developerRepository.findAll(pageable)).thenReturn(page);
        when(developerMapper.toResponse(d1)).thenReturn(r1);

        Page<DeveloperResponse> result = developerService.findAll(pageable);
        assertEquals(1, result.getContent().size());
        assertEquals("Henry", result.getContent().get(0).name());
    }

    @Test
    @DisplayName("CP06 - Obtener developer por ID válido")
    void getDeveloperById_found_returnsDeveloper() {
        Developer dev = new Developer(1L, "Henry");
        DeveloperResponse response = new DeveloperResponse(1L, "Henry");

        when(developerRepository.findById(1L)).thenReturn(Optional.of(dev));
        when(developerMapper.toResponse(dev)).thenReturn(response);

        DeveloperResponse result = developerService.findById(1L);
        assertEquals("Henry", result.name());
    }

    @Test
    @DisplayName("CP07 - Obtener developer inexistente")
    void getDeveloperById_notFound_throwsException() {
        when(developerRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> developerService.findById(99L));
    }

    @Test
    @DisplayName("CP08 - Actualizar developer con nombre válido")
    void updateDeveloper_valid_updateSuccess() {
        Developer existing = new Developer(1L, "Henry");
        DeveloperRequest request = new DeveloperRequest("Luis");
        Developer updated = new Developer(1L, "Luis");
        DeveloperResponse response = new DeveloperResponse(1L, "Luis");

        when(developerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(developerRepository.existsByName("Luis")).thenReturn(false);
        when(developerRepository.save(existing)).thenReturn(updated);
        when(developerMapper.toResponse(updated)).thenReturn(response);

        DeveloperResponse result = developerService.update(1L, request);
        assertEquals("Luis", result.name());
    }

    @Test
    @DisplayName("CP09 - Actualizar developer con nombre duplicado")
    void updateDeveloper_duplicateName_throwsException() {
        Developer existing = new Developer(1L, "Henry");
        DeveloperRequest request = new DeveloperRequest("Maria");

        when(developerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(developerRepository.existsByName("Maria")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> developerService.update(1L, request));
    }

    @Test
    @DisplayName("CP10 - Actualizar developer inexistente")
    void updateDeveloper_notFound_throwsException() {
        DeveloperRequest request = new DeveloperRequest("Luis");
        when(developerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> developerService.update(1L, request));
    }

    @Test
    @DisplayName("CP11 - Eliminar developer existente")
    void deleteDeveloper_found_executesDelete() {
        Developer dev = new Developer(1L, "Henry");
        when(developerRepository.findById(1L)).thenReturn(Optional.of(dev));
        developerService.delete(1L);
        verify(developerRepository).delete(dev);
    }

    @Test
    @DisplayName("CP12 - Eliminar developer inexistente")
    void deleteDeveloper_notFound_throwsException() {
        when(developerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> developerService.delete(1L));
    }
}
