package application.dto.job;

import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class JobResponseDto {
    private Long id;
    private Long orderId;
    private Long masterId;
    private BigDecimal price;
}
