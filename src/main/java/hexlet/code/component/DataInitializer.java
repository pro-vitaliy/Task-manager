package hexlet.code.component;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.taskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.LabelService;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private LabelService labelService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Override
    public void run(String... args) {
        var userData = new UserCreateDTO("hexlet@example.com", "qwerty");
        if (!userRepository.existsByEmail(userData.getEmail())) {
            userService.create(userData);
        }


        List<TaskStatusCreateDTO> taskStatusesData = List.of(
                new TaskStatusCreateDTO("draft", "draft"),
                new TaskStatusCreateDTO("toReview", "to_review"),
                new TaskStatusCreateDTO("toBeFixed", "to_be_fixed"),
                new TaskStatusCreateDTO("toPublish", "to_publish"),
                new TaskStatusCreateDTO("published", "published")
        );

        taskStatusesData.forEach(statusData -> {
            if (!taskStatusRepository.existsByNameOrSlug(statusData.getName(), statusData.getSlug())) {
                taskStatusService.create(statusData);
            }
        });

        List<LabelCreateDTO> labels = List.of(
                new LabelCreateDTO("feature"),
                new LabelCreateDTO("bug")
        );
        labels.forEach(labelData -> {
            if (!labelRepository.existsByName(labelData.getName())) {
                labelService.create(labelData);
            }
        });
    }
}
