package application.service;

import application.dto.order.OrderResponseDto;
import application.dto.owner.OwnerResponseDto;
import java.util.List;

public interface OwnerService {
    OwnerResponseDto createOwner();

    List<OrderResponseDto> getAllOrdersByOwnerId(Long id);
}
