package hexlet.code.component;

import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.LabelService;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import lombok.RequiredArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserService userService;
    private final TaskStatusService taskStatusService;
    private final LabelService labelService;
    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final LabelRepository labelRepository;

    @Override
    public void run(String... args) {
        var userData = new UserDTO(
                JsonNullable.of("hexlet@example.com"),
                JsonNullable.of("qwerty")
        );

        if (!userRepository.existsByEmail(userData.getEmail().get())) {
            userService.create(userData);
        }

        List<TaskStatusDTO> taskStatusesData = List.of(
                new TaskStatusDTO(JsonNullable.of("draft"), JsonNullable.of("draft")),
                new TaskStatusDTO(JsonNullable.of("toReview"), JsonNullable.of("to_review")),
                new TaskStatusDTO(JsonNullable.of("toBeFixed"), JsonNullable.of("to_be_fixed")),
                new TaskStatusDTO(JsonNullable.of("toPublish"), JsonNullable.of("to_publish")),
                new TaskStatusDTO(JsonNullable.of("published"), JsonNullable.of("published"))
        );

        taskStatusesData.forEach(statusData -> {
            if (!taskStatusRepository.existsByNameOrSlug(statusData.getName().get(), statusData.getSlug().get())) {
                taskStatusService.create(statusData);
            }
        });

        List<LabelDTO> labels = List.of(
                new LabelDTO(JsonNullable.of("feature")),
                new LabelDTO(JsonNullable.of("bug"))
        );
        labels.forEach(labelData -> {
            if (!labelRepository.existsByName(labelData.getName().get())) {
                labelService.create(labelData);
            }
        });
    }
}
