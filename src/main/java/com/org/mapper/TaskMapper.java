package com.org.mapper;

import com.org.dto.request.TaskRequest;
import com.org.dto.response.TaskResponse;
import com.org.model.Developer;
import com.org.model.Task;
import com.org.model.enums.TaskStatus;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toEntity(TaskRequest request, Developer developer) {
        return Task.builder()
                .title(request.title())
                .description(request.description())
                .developer(developer)
                .status(TaskStatus.PENDING)
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build();
    }


    public TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus().name(),
                task.getDeveloper().getName(),
                task.getStartDate(),
                task.getEndDate()
        );
    }
}
