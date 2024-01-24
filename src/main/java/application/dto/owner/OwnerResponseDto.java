package application.dto.owner;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OwnerResponseDto {
    private Long id;
}
