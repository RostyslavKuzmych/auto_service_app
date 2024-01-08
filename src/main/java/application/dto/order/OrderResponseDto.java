package application.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderResponseDto {
    private Long id;
    private Long carId;
    private String problemDescription;
    private LocalDateTime dateOfAcceptance;
    private Set<Long> jobsId;
    private Set<Long> goodsId;
    private BigDecimal finalAmount;
    private LocalDateTime endDate;
    private String status;
}
