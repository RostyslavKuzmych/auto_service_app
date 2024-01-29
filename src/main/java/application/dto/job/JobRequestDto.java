package application.dto.job;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
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
