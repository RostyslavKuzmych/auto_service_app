package application.dto.good;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GoodRequestDto {
    @NotNull
    private String name;
    @NotNull
    private BigDecimal price;
}
