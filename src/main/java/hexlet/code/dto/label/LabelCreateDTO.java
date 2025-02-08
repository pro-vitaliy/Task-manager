package hexlet.code.dto.label;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LabelCreateDTO {
    @NotNull
    @Size(min = 3, max = 1000)
    private String name;
}
