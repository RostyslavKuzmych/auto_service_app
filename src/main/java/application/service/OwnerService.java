package application.service;

import application.dto.order.OrderResponseDto;
import application.dto.owner.OwnerRequestDto;
import application.dto.owner.OwnerResponseDto;
import application.dto.owner.OwnerResponseDtoWithCars;
import java.util.List;

public interface OwnerService {
    OwnerResponseDto createOwner();

    OwnerResponseDtoWithCars updateOwnerByid(Long id, OwnerRequestDto ownerRequestDto);

    List<OrderResponseDto> getAllOrdersByOwnerId(Long id);
}
