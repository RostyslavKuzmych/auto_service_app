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
    private static final String EXCEPTION_ORDER = "Can't find owner by order id ";
    private static final String EXCEPTION_CAR = "Can't find car by id ";
    private final OwnerRepository ownerRepository;
    private final OwnerMapper ownerMapper;
    private final OrderMapper orderMapper;
    private final CarRepository carRepository;
    private final OrderRepository orderRepository;

    @Override
    public OwnerResponseDto createOwner() {
        return ownerMapper.toDto(ownerRepository.save(new Owner()));
    }

    @Override
    @Transactional
    public OwnerResponseDtoWithCars updateOwnerByid(Long id, OwnerRequestDto ownerRequestDto) {
        checkIfOwnerExists(id);
        ownerRequestDto.getCarsId().stream()
                .map(carId -> findCarById(carId))
                .map(car -> car.setOwner(new Owner().setId(id)))
                .forEach(carRepository::save);
        List<Order> orders = ownerRequestDto.getCarsId().stream()
                .flatMap(carId -> orderRepository.findAllByCarId(carId).stream())
                .toList();
        checkIfOwnerHasOrders(id, orders);
        return ownerMapper.toDtoWithCars(findByIdWithCars(id));
    }

    @Override
    public List<OrderResponseDto> getAllOrdersByOwnerId(Long id) {
        return findByIdWithOrders(id).getOrders().stream().map(orderMapper::toDto).toList();
    }

    private void checkIfOwnerExists(Long id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EXCEPTION + id));
    }

    private Car findCarById(Long carId) {
        return carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException(EXCEPTION_CAR + carId));
    }

    private void checkIfOwnerHasOrders(Long id, List<Order> orders) {
        Owner owner = findByIdWithOrders(id);
        orders.stream().forEach(order -> {
            Owner ownerFromDb = ownerRepository.findByOrderId(order.getId())
                    .orElseThrow(() -> new EntityNotFoundException(EXCEPTION_ORDER + id));
            if (ownerFromDb.getId() != id) {
                ownerFromDb.getOrders().remove(order);
                ownerRepository.save(ownerFromDb);
                owner.getOrders().add(order);
                ownerRepository.save(owner);
            }
        });
    }

    private Owner findByIdWithCars(Long ownerId) {
        return ownerRepository.findByIdWithCars(ownerId)
                .orElseThrow(() -> new EntityNotFoundException(EXCEPTION + ownerId));
    }

    private Owner findByIdWithOrders(Long orderId) {
        return ownerRepository.findByIdWithOrders(orderId).orElseThrow(()
                -> new EntityNotFoundException(EXCEPTION + orderId));
    }
}
