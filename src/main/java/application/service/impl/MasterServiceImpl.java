package application.service.impl;

import application.dto.master.MasterRequestDto;
import application.dto.master.MasterResponseDto;
import application.dto.order.OrderResponseDto;
import application.exception.EntityNotFoundException;
import application.mapper.MasterMapper;
import application.mapper.OrderMapper;
import application.model.Job;
import application.model.Master;
import application.repository.JobRepository;
import application.repository.MasterRepository;
import application.service.MasterService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MasterServiceImpl implements MasterService {
    private static final Double MASTER_PERCENT = 0.4;
    private static final String EXCEPTION = "Can't find master by id ";
    private final MasterMapper masterMapper;
    private final OrderMapper orderMapper;
    private final MasterRepository masterRepository;
    private final JobRepository jobRepository;

    @Override
    @Transactional
    public MasterResponseDto createMaster(MasterRequestDto masterRequestDto) {
        return masterMapper.toDto(masterRepository
                .save(masterMapper.toEntity(masterRequestDto)));
    }

    @Override
    @Transactional(readOnly = true)
    public MasterResponseDto updateById(Long id, MasterRequestDto masterRequestDto) {
        Optional<Master> master = masterRepository.findById(id);
        if (master.isPresent()) {
            Master masterFromDb = masterMapper.toEntity(masterRequestDto).setId(id);
            return masterMapper.toDto(masterRepository.save(masterFromDb));
        }
        throw new EntityNotFoundException(EXCEPTION + id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllSuccessfulOrdersByMasterId(Long id) {
        return findByIdWithAllOrders(id)
                .getOrders().stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    /*
     * The master's salary is 40% of all completed tasks
     * for which he has not yet received payment.
     */
    public BigDecimal getSalaryByMasterId(Long id) {
        Set<Job> unpaidJobs = getUnpaidJobs(id);
        unpaidJobs.forEach(c -> jobRepository.save(c.setStatus(Job.Status.PAID)));
        BigDecimal priceOfJobs = getPriceOfJobs(unpaidJobs);
        return priceOfJobs.multiply(BigDecimal.valueOf(MASTER_PERCENT));
    }

    private BigDecimal getPriceOfJobs(Set<Job> unpaidJobs) {
        return unpaidJobs.stream()
                .map(Job::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Set<Job> getUnpaidJobs(Long id) {
        return findByIdWithAllOrders(id)
                .getOrders().stream()
                .flatMap(order -> order.getJobs().stream())
                .filter(job -> Objects.equals(job.getMaster().getId(), id))
                .filter(job -> job.getStatus() == Job.Status.UNPAID)
                .collect(Collectors.toSet());
    }

    private Master findByIdWithAllOrders(Long id) {
        return masterRepository.findMasterById(id).orElseThrow(()
                -> new EntityNotFoundException(EXCEPTION + id));
    }

}
