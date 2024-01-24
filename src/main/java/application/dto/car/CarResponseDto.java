package application.dto.car;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CarResponseDto {
    private Long id;
    private String brand;
    private String model;
    private Integer manufactureYear;
    private Long ownerId;
    private String number;
}
