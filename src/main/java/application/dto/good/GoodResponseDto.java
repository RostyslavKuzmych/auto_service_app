package application.dto.good;

import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GoodResponseDto {
    private Long id;
    private String name;
    private BigDecimal price;
}
