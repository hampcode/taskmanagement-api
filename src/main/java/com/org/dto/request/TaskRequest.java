package com.org.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TaskRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull Long developerId,
        LocalDate startDate,
        LocalDate endDate
) {}
