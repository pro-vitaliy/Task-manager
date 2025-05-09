package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import hexlet.code.dto.task.TaskDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;

import jakarta.transaction.Transactional;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskMapper taskMapper;

    private Task testTask;
    private User testAssignee;
    private TaskStatus testStatus;
    private Label testLabel;

    @BeforeEach
    void beforeEach() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();
        labelRepository.deleteAll();

        testAssignee = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testAssignee);

        testStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(testStatus);

        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);

        testTask = Instancio.of(modelGenerator.getTaskModel()).create();
        testTask.setAssignee(testAssignee);
        testTask.setTaskStatus(testStatus);
        testTask.getLabels().add(testLabel);
        taskRepository.save(testTask);
    }

    @Test
    void testShow() throws Exception {
        var request = get("/api/tasks/" + testTask.getId()).with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("id").isEqualTo(testTask.getId()),
                v -> v.node("index").isEqualTo(testTask.getIndex()),
                v -> v.node("status").isEqualTo(testTask.getTaskStatus().getSlug()),
                v -> v.node("assignee_id").isEqualTo(testTask.getAssignee().getId())
        );
    }

    @Test
    void testIndex() throws Exception {
        var request = get("/api/tasks").with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        assertThatJson(result.getResponse().getContentAsString()).isArray();
    }

    @Test
    @Transactional
    void testCreate() throws Exception {
        var taskModel = Instancio.of(modelGenerator.getTaskModel()).create();
        var taskData = new TaskDTO();
        taskData.setTitle(JsonNullable.of("new task"));
        taskData.setStatus(JsonNullable.of(testTask.getTaskStatus().getSlug()));
        taskData.setIndex(JsonNullable.of(taskModel.getIndex()));
        taskData.setAssigneeId(JsonNullable.of(testAssignee.getId()));
        taskData.setContent(JsonNullable.of(taskModel.getDescription()));
        taskData.setTaskLabelIds(JsonNullable.of(Set.of(testLabel.getId())));

        var request = post("/api/tasks")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskData));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        Optional<Task> expectedTask = taskRepository.findByName(taskData.getTitle().get());

        assertThat(expectedTask)
                .isPresent()
                .hasValueSatisfying(task -> {
                    assertThat(task.getName()).isEqualTo("new task");
                    assertThat(task.getTaskStatus().getSlug()).isEqualTo(taskData.getStatus().get());
                    assertThat(task.getLabels()).contains(testLabel);
                    assertThat(task.getAssignee()).isEqualTo(testAssignee);
                    assertThat(task.getCreatedAt()).isNotNull();
                });
    }

    @Test
    @Transactional
    void testUpdate() throws Exception {
        var taskData = new HashMap<String, String>();
        taskData.put("title", "New title");

        var request = put("/api/tasks/" + testTask.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskData));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        Optional<Task> expectedTask = taskRepository.findById(testTask.getId());
        assertThat(expectedTask).isPresent();
        assertThat(expectedTask.get().getName()).isEqualTo("New title");
    }

    @Test
    void testDelete() throws Exception {
        var request = delete("/api/tasks/" + testTask.getId()).with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        Optional<Task> expectedEmpty = taskRepository.findById(testTask.getId());
        assertThat(expectedEmpty).isEmpty();
    }

    @Test
    @Transactional
    void testUpdateLabels() throws Exception {
        var taskData = new HashMap<String, Object>();
        taskData.put("labels", Set.of(testLabel.getName()));

        var request = put("/api/tasks/" + testTask.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskData));
        mockMvc.perform(request).andExpect(status().isOk());

        Optional<Task> expected = taskRepository.findById(testTask.getId());
        assertThat(expected).isPresent();
        assertThat(expected.get().getLabels()).contains(testLabel);
    }

    @Test
    void testIndexWithParamAssignee() throws Exception {
        var request = get("/api/tasks?assigneeId=" + testAssignee.getId()).with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        List<TaskDTO> actual = objectMapper.readValue(body, new TypeReference<>() { });

        assertThat(actual)
                .isNotEmpty()
                .allMatch(task -> task.getAssigneeId().get().equals(testAssignee.getId()));
    }

    @Test
    void testIndexWithParamTitle() throws Exception {
        var request = get("/api/tasks?titleCont=" + testTask.getName()).with(jwt());
        var body = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<TaskDTO> actual = objectMapper.readValue(body, new TypeReference<>() { });

        assertThat(actual)
                .isNotEmpty()
                .allMatch(task -> task.getId().equals(testTask.getId()));
    }
}
