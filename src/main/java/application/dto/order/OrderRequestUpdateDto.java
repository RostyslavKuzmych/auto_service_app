package application.dto.order;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderRequestUpdateDto {
    private String problemDescription;
    private Long carId;
}
