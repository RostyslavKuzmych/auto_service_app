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
    public OwnerResponseDto createOwner(OwnerRequestDto ownerRequestDto) {
        return ownerMapper.toDto(ownerRepository.save(ownerMapper.toEntity(ownerRequestDto)));
    }

    @Override
    public List<OrderResponseDto> getAllOrdersByOwnerId(Long id) {
        return findByIdWithOrders(id)
                .getOrders().stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OwnerResponseDto updateOwner(Long id, OwnerRequestDto ownerRequestDto) {
        Owner owner = ownerRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(EXCEPTION + id));
        owner.setPhoneNumber(ownerRequestDto.getPhoneNumber())
                .setFirstName(ownerRequestDto.getFirstName())
                .setLastName(ownerRequestDto.getLastName());
        return ownerMapper.toDto(ownerRepository.save(owner));
    }

    private Owner findByIdWithOrders(Long id) {
        return ownerRepository.findByIdWithOrders(id).orElseThrow(()
                -> new EntityNotFoundException(EXCEPTION + id));
    }
}
