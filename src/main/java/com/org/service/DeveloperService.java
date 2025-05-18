package com.org.service;

import com.org.dto.request.DeveloperRequest;
import com.org.dto.response.DeveloperResponse;
import com.org.exception.DuplicateResourceException;
import com.org.exception.ResourceNotFoundException;
import com.org.mapper.DeveloperMapper;
import com.org.model.Developer;
import com.org.repository.DeveloperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeveloperService {

    private final DeveloperRepository developerRepository;
    private final DeveloperMapper developerMapper;

    @Transactional
    public DeveloperResponse create(DeveloperRequest request) {
        if (developerRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Ya existe un developer con ese nombre");
        }
        Developer saved = developerRepository.save(developerMapper.toEntity(request));
        return developerMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<DeveloperResponse> findAll() {
        return developerRepository.findAll()
                .stream()
                .map(developerMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<DeveloperResponse> findPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return developerRepository.findAll(pageable)
                .map(developerMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public DeveloperResponse findById(Long id) {
        Developer dev = developerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Developer no encontrado"));
        return developerMapper.toResponse(dev);
    }

    @Transactional
    public DeveloperResponse update(Long id, DeveloperRequest request) {
        Developer dev = developerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Developer no encontrado"));

        if (!dev.getName().equals(request.name()) && developerRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Ya existe otro developer con ese nombre");
        }

        dev.setName(request.name());
        return developerMapper.toResponse(developerRepository.save(dev));
    }

    @Transactional
    public void delete(Long id) {
        Developer dev = developerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Developer no encontrado"));

        developerRepository.delete(dev);
    }
}
