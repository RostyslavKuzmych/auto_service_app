package application.service.impl;

import application.dto.order.OrderResponseDto;
import application.dto.owner.OwnerRequestDto;
import application.dto.owner.OwnerResponseDto;
import application.exception.EntityNotFoundException;
import application.mapper.OrderMapper;
import application.mapper.OwnerMapper;
import application.model.Owner;
import application.repository.OwnerRepository;
import application.service.OwnerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OwnerServiceImpl implements OwnerService {
    private static final String EXCEPTION = "Can't find owner by id ";
    private final OwnerRepository ownerRepository;
    private final OwnerMapper ownerMapper;
    private final OrderMapper orderMapper;

    @Override
    public OwnerResponseDto createOwner() {
        return ownerMapper.toDto(ownerRepository.save(new Owner()));
    }

    @Override
    public OwnerResponseDto updateOwnerByid(Long id, OwnerRequestDto ownerRequestDto) {
        checkIfOwnerExists(id);
        Owner newOwner = ownerMapper.toEntity(ownerRequestDto).setId(id);
        return ownerMapper.toDto(ownerRepository.save(newOwner));
    }

    @Override
    public List<OrderResponseDto> getAllOrdersByOwnerId(Long id) {
        Owner owner = ownerRepository.findByIdWithOrders(id).orElseThrow(()
                -> new EntityNotFoundException(EXCEPTION + id));
        return owner.getOrders().stream().map(orderMapper::toDto).toList();
    }

    private void checkIfOwnerExists(Long id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EXCEPTION + id));
    }
}
