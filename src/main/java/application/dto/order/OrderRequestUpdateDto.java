package application.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderRequestUpdateDto {
    @NotBlank
    private String problemDescription;
}
