package application.service;

import application.dto.order.OrderResponseDto;
import application.dto.owner.OwnerRequestDto;
import application.dto.owner.OwnerResponseDto;
import java.util.List;

public interface OwnerService {
    OwnerResponseDto createOwner(OwnerRequestDto ownerRequestDto);

    List<OrderResponseDto> getAllOrdersByOwnerId(Long id);

    OwnerResponseDto updateOwner(Long id, OwnerRequestDto ownerRequestDto);
}
