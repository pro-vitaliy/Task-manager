package hexlet.code.dto.taskstatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class TaskStatusDTO {

    private Long id;

    @NotBlank
    @Size(min = 1)
    private JsonNullable<String> name = JsonNullable.undefined();

    @NotBlank
    @Size(min = 1)
    private JsonNullable<String> slug = JsonNullable.undefined();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    public TaskStatusDTO(JsonNullable<String> name, JsonNullable<String> slug) {
        this.name = name;
        this.slug = slug;
    }
}
