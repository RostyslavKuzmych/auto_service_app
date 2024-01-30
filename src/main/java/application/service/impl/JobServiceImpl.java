package application.service.impl;

import application.dto.job.JobRequestDto;
import application.dto.job.JobRequestStatusDto;
import application.dto.job.JobResponseDto;
import application.exception.EntityNotFoundException;
import application.mapper.JobMapper;
import application.model.Job;
import application.repository.JobRepository;
import application.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class JobServiceImpl implements JobService {
    private static final String EXCEPTION = "Can't find job by id ";
    private final JobRepository jobRepository;
    private final JobMapper jobMapper;

    @Override
    @Transactional
    public JobResponseDto createJob(JobRequestDto jobRequestDto) {
        return jobMapper.toDto(jobRepository.save(jobMapper.toEntity(jobRequestDto)));
    }

    @Override
    @Transactional
    public JobResponseDto updateJob(Long id, JobRequestDto jobRequestDto) {
        if (jobRepository.findById(id).isPresent()) {
            Job job = jobMapper.toEntity(jobRequestDto).setId(id);
            return jobMapper.toDto(jobRepository.save(job));
        }
        throw new EntityNotFoundException(EXCEPTION + id);
    }

    @Override
    @Transactional
    public JobResponseDto updateJobStatus(Long id, JobRequestStatusDto status) {
        Job job = findById(id).setStatus(status.getStatus());
        return jobMapper.toDto(jobRepository.save(job));
    }

    private Job findById(Long id) {
        return jobRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException(EXCEPTION + id));
    }
}
