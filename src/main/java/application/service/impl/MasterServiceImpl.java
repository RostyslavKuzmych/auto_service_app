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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MasterServiceImpl implements MasterService {
    private static final String EXCEPTION = "Can't find master by id ";
    private final MasterMapper masterMapper;
    private final OrderMapper orderMapper;
    private final MasterRepository masterRepository;
    private final JobRepository jobRepository;

    @Override
    public MasterResponseDto createMaster(MasterRequestDto masterRequestDto) {
        return masterMapper.toDto(masterRepository
                .save(masterMapper.toEntity(masterRequestDto)));
    }

    @Override
    public MasterResponseDto updateById(Long id, MasterRequestDto masterRequestDto) {
        checkIfMasterExists(id);
        Master master = masterMapper.toEntity(masterRequestDto).setId(id);
        return masterMapper.toDto(masterRepository.save(master));
    }

    @Override
    public List<OrderResponseDto> getAllSuccessfulOrdersByMasterId(Long id) {
        Master master = findByIdWithAllOrders(id);
        return master.getOrders().stream().map(orderMapper::toDto).toList();
    }

    @Override
    public BigDecimal getSalaryByMasterId(Long id) {
        Master master = findByIdWithAllOrders(id);
        BigDecimal salary = master.getOrders().stream()
                .flatMap(order -> order.getJobs().stream())
                .filter(job -> Objects.equals(job.getMaster().getId(), master.getId()))
                .map(job -> jobRepository.save(job.setStatus(Job.Status.PAID)))
                .map(Job::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return salary.multiply(BigDecimal.valueOf(0.4));
    }

    private void checkIfMasterExists(Long id) {
        Master master = masterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EXCEPTION + id));
    }

    private Master findByIdWithAllOrders(Long id) {
        return masterRepository.findByIdWithAllOrders(id).orElseThrow(()
                -> new EntityNotFoundException(EXCEPTION + id));
    }
}
