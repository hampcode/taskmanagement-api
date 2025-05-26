package com.org.service;

import com.org.dto.request.TaskRequest;
import com.org.dto.response.TaskResponse;
import com.org.exception.BusinessRuleException;
import com.org.exception.DuplicateResourceException;
import com.org.exception.ResourceNotFoundException;
import com.org.mapper.TaskMapper;
import com.org.model.Developer;
import com.org.model.Task;
import com.org.model.enums.TaskStatus;
import com.org.repository.DeveloperRepository;
import com.org.repository.TaskRepository;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceUnitTest {

    @Mock private TaskRepository taskRepository;
    @Mock private DeveloperRepository developerRepository;
    @Mock private TaskMapper taskMapper;
    @InjectMocks private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("CP13 - Crear tarea válida")
    void create_validTask_returnsCreated() {
        TaskRequest request = new TaskRequest("Tarea 1", "Desc", 1L,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 15));
        Developer dev = new Developer(1L, "Henry");
        Task task = Task.builder()
                .id(1L)
                .title("Tarea 1")
                .description("Desc")
                .status(TaskStatus.PENDING)
                .developer(dev)
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build();

        TaskResponse response = new TaskResponse(1L, "Tarea 1", "Desc", "PENDING",
                "Henry", request.startDate(), request.endDate());

        when(taskRepository.existsByTitle("Tarea 1")).thenReturn(false);
        when(developerRepository.findById(1L)).thenReturn(Optional.of(dev));
        when(taskRepository.countActiveTasksByDeveloperId(1L, List.of(TaskStatus.PENDING, TaskStatus.IN_PROGRESS))).thenReturn(0);
        when(taskMapper.toEntity(request, dev)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.create(request);

        assertEquals("Tarea 1", result.title());
        assertEquals("PENDING", result.status());
        assertEquals("Henry", result.developerName());
    }

    @Test
    @DisplayName("CP14 - Crear tarea con título duplicado")
    void create_duplicateTitle_throwsException() {
        TaskRequest request = new TaskRequest("Tarea 1", "Desc", 1L, null, null);
        when(taskRepository.existsByTitle("Tarea 1")).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> taskService.create(request));
    }

    @Test
    @DisplayName("CP15 - Crear tarea sin título o descripción")
    void create_nullFields_throwsException() {
        TaskRequest request = new TaskRequest(null, null, 1L, null, null);
        Developer dev = new Developer(1L, "Henry");
        when(taskRepository.existsByTitle(null)).thenReturn(false);
        when(developerRepository.findById(1L)).thenReturn(Optional.of(dev));
        when(taskRepository.countActiveTasksByDeveloperId(1L, List.of(TaskStatus.PENDING, TaskStatus.IN_PROGRESS))).thenReturn(0);
        when(taskMapper.toEntity(request, dev)).thenThrow(new IllegalArgumentException("Datos inválidos"));
        assertThrows(IllegalArgumentException.class, () -> taskService.create(request));
    }

    @Test
    @DisplayName("CP16 - Crear tarea con developer inexistente")
    void create_invalidDeveloper_throwsException() {
        TaskRequest request = new TaskRequest("Tarea 1", "Desc", 99L, null, null);
        when(taskRepository.existsByTitle("Tarea 1")).thenReturn(false);
        when(developerRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> taskService.create(request));
    }

    @Test
    @DisplayName("CP17 - Crear tarea con developer con exceso activo")
    void create_developerOverLimit_throwsException() {
        TaskRequest request = new TaskRequest("Tarea 1", "Desc", 1L, null, null);
        Developer dev = new Developer(1L, "Henry");
        when(taskRepository.existsByTitle("Tarea 1")).thenReturn(false);
        when(developerRepository.findById(1L)).thenReturn(Optional.of(dev));
        when(taskRepository.countActiveTasksByDeveloperId(1L, List.of(TaskStatus.PENDING, TaskStatus.IN_PROGRESS))).thenReturn(5);
        assertThrows(BusinessRuleException.class, () -> taskService.create(request));
    }



    @Test
    @DisplayName("CP18 - PENDING → IN_PROGRESS (válido)")
    void changeStatus_pendingToInProgress_success() {
        Task task = Task.builder().id(1L).title("T1").description("D1").status(TaskStatus.PENDING).build();
        TaskResponse response = new TaskResponse(1L, "T1", "D1", "IN_PROGRESS", "Henry", null, null);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.changeStatus(1L, TaskStatus.IN_PROGRESS);
        assertEquals("IN_PROGRESS", result.status());
    }

    @Test
    @DisplayName("CP19 - PENDING → COMPLETED (inválido)")
    void changeStatus_pendingToCompleted_invalid() {
        Task task = Task.builder().id(1L).status(TaskStatus.PENDING).build();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        assertThrows(BusinessRuleException.class, () -> taskService.changeStatus(1L, TaskStatus.COMPLETED));
    }

    @Test
    @DisplayName("CP20 - COMPLETED → IN_PROGRESS (inválido)")
    void changeStatus_completedToInProgress_invalid() {
        Task task = Task.builder().id(1L).status(TaskStatus.COMPLETED).build();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        assertThrows(BusinessRuleException.class, () -> taskService.changeStatus(1L, TaskStatus.IN_PROGRESS));
    }

    @Test
    @DisplayName("CP21 - IN_PROGRESS → COMPLETED")
    void changeStatus_inProgressToCompleted_success() {
        Task task = Task.builder().id(1L).title("T1").description("D1").status(TaskStatus.IN_PROGRESS).build();
        TaskResponse response = new TaskResponse(1L, "T1", "D1", "COMPLETED", "Henry", null, null);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.changeStatus(1L, TaskStatus.COMPLETED);
        assertEquals("COMPLETED", result.status());
    }

    @Test
    @DisplayName("CP22 - Obtener tarea existente")
    void getTaskById_found() {
        Task task = Task.builder().id(1L).title("T1").description("D1").status(TaskStatus.PENDING).build();
        TaskResponse response = new TaskResponse(1L, "T1", "D1", "PENDING", "Henry", null, null);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.findById(1L);
        assertEquals("T1", result.title());
    }

    @Test
    @DisplayName("CP23 - Obtener tarea inexistente")
    void getTaskById_notFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> taskService.findById(99L));
    }

    @Test
    @DisplayName("CP24 - Buscar tareas en rango válido")
    void findByDateRange_valid() {
        Task task = Task.builder().id(1L).title("T1").description("D1").status(TaskStatus.PENDING).build();
        TaskResponse response = new TaskResponse(1L, "T1", "D1", "PENDING", "Henry", null, null);
        Pageable pageable = PageRequest.of(0, 5);

        when(taskRepository.findTasksByDateRange(any(), any(), eq(pageable))).thenReturn(new PageImpl<>(List.of(task)));
        when(taskMapper.toResponse(task)).thenReturn(response);

        Page<TaskResponse> result = taskService.findByDateRange(LocalDate.now().minusDays(1), LocalDate.now(), pageable);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("CP25 - Buscar tareas sin resultados")
    void findByDateRange_empty() {
        Pageable pageable = PageRequest.of(0, 5);
        when(taskRepository.findTasksByDateRange(any(), any(), eq(pageable))).thenReturn(new PageImpl<>(Collections.emptyList()));
        Page<TaskResponse> result = taskService.findByDateRange(LocalDate.now().minusDays(10), LocalDate.now(), pageable);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("CP26 - Eliminar tarea existente")
    void deleteTask_found_executesDelete() {
        Task task = Task.builder().id(1L).title("T1").build();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        taskService.delete(1L);
        verify(taskRepository).delete(task);
    }

    @Test
    @DisplayName("CP27 - Eliminar tarea inexistente")
    void deleteTask_notFound_throwsException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> taskService.delete(1L));
    }

    @Test
    @DisplayName("CP28 - Consultar tareas activas de developer válido")
    void findActiveTasksByDeveloper_valid() {
        Developer dev = new Developer(1L, "Henry");
        Task task = Task.builder().id(1L).title("T1").status(TaskStatus.IN_PROGRESS).developer(dev).build();
        TaskResponse response = new TaskResponse(1L, "T1", "Desc", "IN_PROGRESS", "Henry", null, null);

        when(taskRepository.findTasksByDeveloperIdAndStatusIn(eq(1L), any())).thenReturn(List.of(task));
        when(taskMapper.toResponse(task)).thenReturn(response);

        List<TaskResponse> result = taskService.findActiveTasksByDeveloper(1L);
        assertEquals(1, result.size());
        assertEquals("T1", result.getFirst().title());
    }

    @Test
    @DisplayName("CP29 - Consultar developer inexistente")
    void findActiveTasksByDeveloper_empty() {
        when(taskRepository.findTasksByDeveloperIdAndStatusIn(eq(99L), any())).thenReturn(Collections.emptyList());
        List<TaskResponse> result = taskService.findActiveTasksByDeveloper(99L);
        assertTrue(result.isEmpty());
    }


    @Test
    @DisplayName("CP30 - Actualizar tarea con datos válidos y mismo developer")
    void update_validSameDeveloper_success() {
        Task existing = Task.builder().id(1L).title("T1").description("D1").status(TaskStatus.PENDING).developer(new Developer(1L, "Henry")).build();
        TaskRequest request = new TaskRequest("T1", "Actualizado", 1L, null, null);
        TaskResponse response = new TaskResponse(1L, "T1", "Actualizado", "PENDING", "Henry", null, null);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taskRepository.existsByTitle("T1")).thenReturn(false);
        when(developerRepository.findById(1L)).thenReturn(Optional.of(existing.getDeveloper()));
        when(taskRepository.save(existing)).thenReturn(existing);
        when(taskMapper.toResponse(existing)).thenReturn(response);

        TaskResponse result = taskService.update(1L, request);
        assertEquals("Actualizado", result.description());
    }

    @Test
    @DisplayName("CP31 - Obtener todas las tareas paginadas")
    void findAll_pagedList_success() {
        Task task = Task.builder().id(1L).title("T1").description("D1").status(TaskStatus.PENDING).build();
        TaskResponse response = new TaskResponse(1L, "T1", "D1", "PENDING", "Henry", null, null);
        Pageable pageable = PageRequest.of(0, 10);

        when(taskRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(task)));
        when(taskMapper.toResponse(task)).thenReturn(response);

        Page<TaskResponse> result = taskService.findAll(pageable);
        assertEquals(1, result.getTotalElements());
        assertEquals("T1", result.getContent().getFirst().title());
    }
}
