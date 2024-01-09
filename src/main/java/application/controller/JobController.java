package application.controller;

import application.dto.job.JobRequestDto;
import application.dto.job.JobRequestStatusDto;
import application.dto.job.JobResponseDto;
import application.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobs")
@Tag(name = "Job management", description = "Endpoints for job management")
public class JobController {
    private final JobService jobService;

    @PostMapping
    @Operation(summary = "Create a job",
            description = "Endpoint for adding a new job to the db")
    @ResponseStatus(HttpStatus.CREATED)
    public JobResponseDto createJob(@Valid @RequestBody JobRequestDto jobRequestDto) {
        return jobService.createJob(jobRequestDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a job", description = "Endpoint for updating a job")
    @ResponseStatus(HttpStatus.OK)
    public JobResponseDto updateJob(@PathVariable Long id,
                                    @Valid @RequestBody JobRequestDto jobRequestDto) {
        return jobService.updateJob(id, jobRequestDto);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update a job status",
            description = "Endpoint for updating a job status")
    @ResponseStatus(HttpStatus.OK)
    public JobResponseDto updateJobStatus(@PathVariable Long id,
                                          @Valid @RequestBody
                                          JobRequestStatusDto status) {
        return jobService.updateJobStatus(id, status);
    }
}
