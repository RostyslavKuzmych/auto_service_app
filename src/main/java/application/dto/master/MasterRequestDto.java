package application.dto.master;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MasterRequestDto {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
