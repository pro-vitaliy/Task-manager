package hexlet.code.util;

import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import jakarta.annotation.PostConstruct;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;

import static org.instancio.Select.field;

@Component
@Getter
public class ModelGenerator {

    private Model<User> userModel;
    private Model<TaskStatus> taskStatusModel;
    private Model<Task> taskModel;
    private Model<Label> labelModel;

    @Autowired
    private Faker faker;

    @PostConstruct
    private void init() {
        userModel = Instancio.of(User.class)
                .ignore(field(User::getId))
                .ignore(field(User::getCreatedAt))
                .ignore(field(User::getUpdatedAt))
                .supply(field(User::getFirstName), () -> faker.name().firstName())
                .supply(field(User::getLastName), () -> faker.name().lastName())
                .supply(field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(field(User::getPassword), () -> faker.internet().password(3, 33))
                .toModel();

        taskStatusModel = Instancio.of(TaskStatus.class)
                .ignore(field(TaskStatus::getId))
                .ignore(field(TaskStatus::getCreatedAt))
                .supply(field(TaskStatus::getName), () -> faker.internet().slug(null, ""))
                .supply(field(TaskStatus::getSlug), () -> faker.internet().slug(null, "_"))
                .toModel();

        taskModel = Instancio.of(Task.class)
                .ignore(field(Task::getId))
                .ignore(field(Task::getCreatedAt))
                .ignore(field(Task::getAssignee))
                .ignore(field(Task::getTaskStatus))
                .ignore(field(Task::getLabels))
                .supply(field(Task::getName), () -> faker.lorem().characters(3, 20))
                .supply(field(Task::getIndex), () -> faker.number().numberBetween(100, 1000))
                .supply(field(Task::getDescription), () -> faker.lorem().sentence(5))
                .toModel();

        labelModel = Instancio.of(Label.class)
                .ignore(field(Label::getId))
                .ignore(field(Label::getCreatedAt))
                .supply(field(Label::getName), () -> faker.lorem().characters(3, 20))
                .toModel();
    }
}
