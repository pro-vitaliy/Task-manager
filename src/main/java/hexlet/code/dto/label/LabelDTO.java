package hexlet.code.dto.label;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class LabelDTO {

    private JsonNullable<Long> id;

    @NotNull
    @Size(min = 3, max = 1000)
    private JsonNullable<String> name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    public LabelDTO(JsonNullable<String> name) {
        this.name = name;
    }
}
