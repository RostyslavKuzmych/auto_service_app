package application.dto.order;

import application.model.Order;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderRequestStatusDto {
    @NotNull
    private Order.Status status;
}
