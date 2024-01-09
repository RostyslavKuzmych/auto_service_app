package application.service;

import application.dto.order.OrderRequestDto;
import application.dto.order.OrderRequestStatusDto;
import application.dto.order.OrderRequestUpdateDto;
import application.dto.order.OrderResponseDto;
import java.math.BigDecimal;

public interface OrderService {
    OrderResponseDto placeOrder(Long carId, OrderRequestDto orderRequestDto);

    OrderResponseDto addGoodToOrder(Long orderId, Long goodId);

    OrderResponseDto updateOrder(OrderRequestUpdateDto requestUpdateDto, Long orderId);

    OrderResponseDto updateOrderStatus(Long orderId, OrderRequestStatusDto status);

    BigDecimal payForOrder(Long orderId, Long carId);
}
