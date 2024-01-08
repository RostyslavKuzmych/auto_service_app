package application.dto.car;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CarRequestDto {
    @NotNull
    private String brand;
    @NotNull
    private String model;
    @NotNull
    private Integer manufactureYear;
    @NotNull
    private Long ownerId;
    @NotNull
    private String number;
}
