package application.controller;

import application.dto.order.OrderRequestDto;
import application.dto.order.OrderRequestUpdateDto;
import application.dto.order.OrderResponseDto;
import application.model.Order;
import application.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order management", description = "Endpoints for order management")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/{carId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Place an order", description = "Endpoint for adding new order to the db")
    public OrderResponseDto placeOrder(@PathVariable Long carId,
                                       @Valid @RequestBody OrderRequestDto orderRequestDto) {
        return orderService.placeOrder(carId, orderRequestDto);
    }

    @PostMapping("/{orderId}/goods/{goodId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a good to the order",
            description = "Endpoint for adding a good to the order")
    public OrderResponseDto addGoodToOrder(@PathVariable Long orderId,
                                           @PathVariable Long goodId) {
        return orderService.addGoodToOrder(orderId, goodId);
    }

    @PutMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update an order",
            description = "Endpoint for updating an order by id")
    public OrderResponseDto updateOrder(@RequestBody
                                            OrderRequestUpdateDto requestUpdateDto,
                                        @PathVariable Long orderId) {
        return orderService.updateOrder(requestUpdateDto, orderId);
    }

    @PutMapping("/{orderId}/status")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update an order status",
            description = "Endpoint for updating an order status")
    public OrderResponseDto updateOrderStatus(@PathVariable Long orderId,
                                              @RequestBody Order.Status status) {
        return orderService.updateOrderStatus(orderId, status);
    }

    @GetMapping("/{orderId}/payment/{carId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Pay for an order",
            description = "Endpoint for paying for an order")
    public BigDecimal payForOrder(@PathVariable Long orderId, @PathVariable Long carId) {
        return orderService.payForOrder(orderId, carId);
    }

}
