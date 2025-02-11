package hexlet.code.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class UserDTO {

    private Long id;

    @Email(message = "Некорректный адрес электронной почты")
    @NotNull
    private JsonNullable<String> email = JsonNullable.undefined();

    private JsonNullable<String> firstName = JsonNullable.undefined();

    private JsonNullable<String> lastName = JsonNullable.undefined();

    @NotBlank(message = "Пароль не должен быть пустым")
    @Size(min = 3, message = "Пароль должен содержать не менее 3-х символов")
    private JsonNullable<String> password = JsonNullable.undefined();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    public UserDTO(JsonNullable<String> email, JsonNullable<String> password) {
        this.email = email;
        this.password = password;
    }
}
