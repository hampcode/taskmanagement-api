package com.org.dto.response;

import java.time.LocalDate;

public record TaskResponse(
        Long id,
        String title,
        String description,
        String status,
        String developerName,
        LocalDate startDate,
        LocalDate endDate
) {}

