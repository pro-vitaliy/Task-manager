package hexlet.code.service;

import hexlet.code.dto.user.UserDTO;
import hexlet.code.exception.EntityDeletionException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final UserMapper userMapper;

    public UserDTO findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with Id " + id + " not found."));
        return userMapper.map(user);
    }

    public List<UserDTO> getAll() {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO create(UserDTO userData) {

        if (!userData.getEmail().isPresent() || !userData.getPassword().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пароль и почта обязательны");
        }

        var user = userMapper.map(userData);
        userRepository.save(user);
        return userMapper.map(user);
    }

    public UserDTO update(UserDTO userData, Long userId) {
//        TODO: Реализовать проверку на уже сущуствующий email
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with Id " + userId + " not found."));
        userMapper.update(userData, user);
        userRepository.save(user);
        return userMapper.map(user);
    }

    public void deleteById(Long id) {
        if (taskRepository.existsByAssigneeId(id)) {
            throw new EntityDeletionException("Нельзя удалить пользователя у которого есть задачи");
        }
        userRepository.deleteById(id);
    }
}
