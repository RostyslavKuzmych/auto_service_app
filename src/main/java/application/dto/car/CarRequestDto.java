package application.dto.car;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CarRequestDto {
    @NotBlank
    private String brand;
    @NotBlank
    private String model;
    @NotNull
    private Integer manufactureYear;
    @NotNull
    private Long ownerId;
    @NotBlank
    private String number;
}
