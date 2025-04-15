package hexlet.code.service;

import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskSpecification;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskSpecification taskSpecification;

    public TaskDTO show(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        return taskMapper.map(task);
    }

    public List<TaskDTO> getAll() {
        return taskRepository.findAll()
                .stream()
                .map(taskMapper::map)
                .toList();
    }

    public List<TaskDTO> getAll(TaskParamsDTO taskParams) {
        var spec = taskSpecification.build(taskParams);
        var filteredTasks = taskRepository.findAll(spec);
        return filteredTasks.stream()
                .map(taskMapper::map)
                .toList();
    }

    public TaskDTO create(TaskDTO taskData) {

        if (!taskData.getStatus().isPresent() || !taskData.getTitle().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Статус и заголовок обязательны");
        }
        var task = taskMapper.map(taskData);
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public TaskDTO update(TaskDTO taskData, Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id \" + id + \" not found"));
        taskMapper.update(taskData, task);
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
