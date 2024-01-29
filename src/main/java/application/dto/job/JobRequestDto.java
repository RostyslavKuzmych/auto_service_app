package application.dto.job;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class JobRequestDto {
    @NotNull
    private Long orderId;
    @NotNull
    private Long masterId;
    @NotNull
    @Positive
    private BigDecimal price;
}
