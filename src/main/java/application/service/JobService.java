package application.service;

import application.dto.job.JobRequestDto;
import application.dto.job.JobRequestStatusDto;
import application.dto.job.JobResponseDto;

public interface JobService {
    JobResponseDto createJob(JobRequestDto jobRequestDto);

    JobResponseDto updateJob(Long id, JobRequestDto jobRequestDto);

    JobResponseDto updateJobStatus(Long id, JobRequestStatusDto status);
}
