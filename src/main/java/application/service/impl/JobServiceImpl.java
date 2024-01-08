package application.service.impl;

import application.dto.job.JobRequestDto;
import application.dto.job.JobResponseDto;
import application.exception.EntityNotFoundException;
import application.mapper.JobMapper;
import application.model.Job;
import application.repository.JobRepository;
import application.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JobServiceImpl implements JobService {
    private static final String EXCEPTION = "Can't find job by id ";
    private final JobRepository jobRepository;
    private final JobMapper jobMapper;

    @Override
    public JobResponseDto createJob(JobRequestDto jobRequestDto) {
        return jobMapper.toDto(jobRepository.save(jobMapper.toEntity(jobRequestDto)
                .setStatus(Job.Status.UNPAID)));
    }

    @Override
    public JobResponseDto updateJob(Long id, JobRequestDto jobRequestDto) {
        checkIfJobExists(id);
        Job job = jobMapper.toEntity(jobRequestDto).setId(id);
        return jobMapper.toDto(jobRepository.save(job));
    }

    @Override
    public JobResponseDto updateJobStatus(Long id, Job.Status status) {
        checkIfJobExists(id);
        Job job = jobRepository.findById(id).get().setStatus(status);
        return jobMapper.toDto(jobRepository.save(job));
    }

    private void checkIfJobExists(Long id) {
        Job job = jobRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException(EXCEPTION + id));
    }
}
