package com.org.controller;

import com.org.dto.request.DeveloperRequest;
import com.org.dto.response.DeveloperResponse;
import com.org.service.DeveloperService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/developers")
@RequiredArgsConstructor
public class DeveloperController {

    private final DeveloperService developerService;

    @PostMapping
    public ResponseEntity<DeveloperResponse> create(@Valid @RequestBody DeveloperRequest request) {
        DeveloperResponse response = developerService.create(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<DeveloperResponse>> getAll() {
        return ResponseEntity.ok(developerService.findAll());
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<DeveloperResponse>> getPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(developerService.findPaginated(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeveloperResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(developerService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeveloperResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody DeveloperRequest request) {
        return ResponseEntity.ok(developerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        developerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
