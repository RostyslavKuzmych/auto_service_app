package application.dto.owner;

import application.validation.PhoneNumber;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OwnerRequestDto {
    @PhoneNumber
    private String phoneNumber;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
