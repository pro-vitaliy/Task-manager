package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.taskStatus.TaskStatusDTO;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.util.ModelGenerator;
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
import java.util.Optional;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskStatus testTaskStatus;

    @BeforeEach
    public void beforeEach() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();

        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(testTaskStatus);
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/task_statuses/" + testTaskStatus.getId()).with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("id").isEqualTo(testTaskStatus.getId()),
                v -> v.node("name").isEqualTo(testTaskStatus.getName()),
                v -> v.node("slug").isEqualTo(testTaskStatus.getSlug())
        );
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/task_statuses").with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        var taskStatusModel = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        var taskStatusData = new TaskStatusDTO(
                JsonNullable.of(taskStatusModel.getName()),
                JsonNullable.of(taskStatusModel.getSlug())
        );

        var request = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskStatusData))
                .with(jwt());

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        Optional<TaskStatus> expectedTaskStatus = taskStatusRepository.findBySlug(taskStatusData.getSlug().get());
        assertThat(expectedTaskStatus)
                .isPresent()
                .hasValueSatisfying(taskStatus -> {
                    assertThat(taskStatus.getName()).isEqualTo(taskStatusData.getName().get());
                    assertThat(taskStatus.getSlug()).isEqualTo(taskStatusData.getSlug().get());
                    assertThat(taskStatus.getCreatedAt()).isNotNull();
                });
    }

    @Test
    public void testUpdate() throws Exception {
        var updateData = new HashMap<String, String>();
        updateData.put("name", "newStatus");

        var request = put("/api/task_statuses/" + testTaskStatus.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData));
        mockMvc.perform(request)
                .andExpect(status().isOk());

        Optional<TaskStatus> expectedTaskStatus = taskStatusRepository.findBySlug(testTaskStatus.getSlug());
        assertThat(expectedTaskStatus).isPresent();
        assertThat(expectedTaskStatus.get().getName()).isEqualTo("newStatus");
    }

    @Test
    public void testDelete() throws Exception {
        var request = delete("/api/task_statuses/" + testTaskStatus.getId()).with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(taskStatusRepository.findBySlug(testTaskStatus.getSlug())).isEmpty();
    }

}
