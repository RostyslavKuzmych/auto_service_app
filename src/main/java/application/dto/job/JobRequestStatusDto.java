package application.dto.job;

import application.model.Job;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class JobRequestStatusDto {
    @NotNull
    private Job.Status status;
}
