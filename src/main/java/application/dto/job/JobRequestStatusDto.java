package application.dto.job;

import application.model.Job;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JobRequestStatusDto {
    @NotNull
    private Job.Status status;
}
