package hexlet.code.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDTO {

    private String firstName;
    private String lastName;

    @Email(message = "Некорректный адрес электронной почты")
    @NotNull
    private String email;

    @NotBlank(message = "Пароль не должен быть пустым")
    @Size(min = 3, message = "Пароль должен содержать не менее 3-х символов")
    private String password;

    public UserCreateDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
