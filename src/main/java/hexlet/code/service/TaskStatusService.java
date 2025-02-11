package hexlet.code.service;


import hexlet.code.dto.taskStatus.TaskStatusDTO;
import hexlet.code.exception.EntityDeletionException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    public TaskStatusDTO show(Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found"));
        return taskStatusMapper.map(taskStatus);
    }

    public List<TaskStatusDTO> getAll() {
        return taskStatusRepository.findAll()
                .stream()
                .map(taskStatusMapper::map)
                .toList();
    }

    public TaskStatusDTO create(TaskStatusDTO data) {
        var taskStatus = taskStatusMapper.map(data);
        taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    public TaskStatusDTO update(TaskStatusDTO data, Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found"));
        taskStatusMapper.update(data, taskStatus);
        taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    public void delete(Long id) {
        if (taskRepository.existsByTaskStatusId(id)) {
            throw new EntityDeletionException("Статус связан с задачей, его нельзя удалить");
        }
        taskStatusRepository.deleteById(id);
    }
}
