package com.org.controller;

import com.org.dto.request.TaskRequest;
import com.org.dto.response.TaskResponse;
import com.org.model.enums.TaskStatus;
import com.org.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // Crear una nueva tarea
    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.create(request));
    }

    // Obtener todas las tareas con paginación
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(taskService.findAll(pageable));
    }

    // Obtener una tarea por su ID
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.findById(id));
    }

    // Actualizar una tarea
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.update(id, request));
    }

    // Eliminar una tarea
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Cambiar el estado de una tarea
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> changeStatus(@PathVariable Long id, @RequestParam TaskStatus status) {
        return ResponseEntity.ok(taskService.changeStatus(id, status));
    }

    // Buscar tareas por rango de fechas
    @GetMapping("/range")
    public ResponseEntity<Page<TaskResponse>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Pageable pageable
    ) {
        return ResponseEntity.ok(taskService.findByDateRange(start, end, pageable));
    }

    // Obtener tareas activas (PENDING o IN_PROGRESS) por developer
    @GetMapping("/developer/{developerId}/active")
    public ResponseEntity<List<TaskResponse>> findActiveByDeveloper(@PathVariable Long developerId) {
        return ResponseEntity.ok(taskService.findActiveTasksByDeveloper(developerId));
    }
}
