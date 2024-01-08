package application.dto.master;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MasterRequestDto {
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
}
