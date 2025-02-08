package hexlet.code.mapper;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.model.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import org.openapitools.jackson.nullable.JsonNullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(
        uses = JsonNullableMapper.class,
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public abstract UserDTO map(User userModel);

    @Mapping(target = "password", expression = "java(encryptPassword(userData))")
    public abstract User map(UserCreateDTO userData);

    @Mapping(
            target = "password",
            expression = "java(encryptPasswordAndUpdate(updateData.getPassword(), model.getPassword()))"
    )
    public abstract void update(UserUpdateDTO updateData, @MappingTarget User model);

    protected String encryptPassword(UserCreateDTO userCreateDTO) {
        return passwordEncoder.encode(userCreateDTO.getPassword());
    }

    protected String encryptPasswordAndUpdate(JsonNullable<String> newPassword, String currentPassword) {
        if (newPassword != null && newPassword.isPresent()) {
            return passwordEncoder.encode(newPassword.get());
        } else {
            return currentPassword;
        }
    }
}
