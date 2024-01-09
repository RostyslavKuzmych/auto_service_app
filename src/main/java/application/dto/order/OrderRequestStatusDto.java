package application.dto.order;

import application.model.Order;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequestStatusDto {
    @NotNull
    private Order.Status status;
}
