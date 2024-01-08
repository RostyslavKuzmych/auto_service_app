package application.service;

import application.dto.job.JobRequestDto;
import application.dto.job.JobResponseDto;
import application.model.Job;

public interface JobService {
    JobResponseDto createJob(JobRequestDto jobRequestDto);

    JobResponseDto updateJob(Long id, JobRequestDto jobRequestDto);

    JobResponseDto updateJobStatus(Long id, Job.Status status);
}
