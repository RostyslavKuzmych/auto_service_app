package application.dto.master;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MasterResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
}
