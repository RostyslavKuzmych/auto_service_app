package application.service.impl;

import application.dto.job.JobRequestDto;
import application.dto.job.JobRequestStatusDto;
import application.dto.job.JobResponseDto;
import application.exception.EntityNotFoundException;
import application.mapper.JobMapper;
import application.model.Job;
import application.model.Master;
import application.model.Order;
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
        Job job = findById(id)
                .setMaster(new Master().setId(jobRequestDto.getMasterId()))
                .setOrder(new Order().setId(jobRequestDto.getOrderId()));
        return jobMapper.toDto(jobRepository.save(job));
    }

    @Override
    public JobResponseDto updateJobStatus(Long id, JobRequestStatusDto status) {
        Job job = findById(id).setStatus(status.getStatus());
        return jobMapper.toDto(jobRepository.save(job));
    }

    private Job findById(Long id) {
        return jobRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException(EXCEPTION + id));
    }
}
