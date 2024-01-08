package application.dto.owner;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OwnerRequestDto {
    @NotNull
    private Set<Long> carsId;
}
