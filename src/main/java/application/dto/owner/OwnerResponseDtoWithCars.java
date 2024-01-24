package application.dto.owner;

import java.util.Set;
import lombok.Data;

@Data
public class OwnerResponseDtoWithCars {
    private Long id;
    private Set<Long> carsId;
}
