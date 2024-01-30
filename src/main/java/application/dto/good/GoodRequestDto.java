package application.dto.good;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GoodRequestDto {
    @NotBlank
    private String name;
    @NotNull
    @Positive
    private BigDecimal price;
}
