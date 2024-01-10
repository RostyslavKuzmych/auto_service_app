package application.service.impl;

import application.dto.order.OrderResponseDto;
import application.dto.owner.OwnerRequestDto;
import application.dto.owner.OwnerResponseDto;
import application.dto.owner.OwnerResponseDtoWithCars;
import application.exception.EntityNotFoundException;
import application.mapper.OrderMapper;
import application.mapper.OwnerMapper;
import application.model.Car;
import application.model.Order;
import application.model.Owner;
import application.repository.CarRepository;
import application.repository.OrderRepository;
import application.repository.OwnerRepository;
import application.service.OwnerService;
import jakarta.transaction.Transactional;
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
    public List<OrderResponseDto> getAllOrdersByOwnerId(Long id) {
        return findByIdWithOrders(id).getOrders().stream().map(orderMapper::toDto).toList();
    }

    private Owner findByIdWithOrders(Long orderId) {
        return ownerRepository.findByIdWithOrders(orderId).orElseThrow(()
                -> new EntityNotFoundException(EXCEPTION + orderId));
    }
}
