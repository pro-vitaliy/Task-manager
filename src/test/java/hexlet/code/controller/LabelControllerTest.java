package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.util.ModelGenerator;

import org.instancio.Instancio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
public class LabelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    private Label testLabel;

    @BeforeEach
    public void beforeEach() {
        taskRepository.deleteAll();
        labelRepository.deleteAll();
        this.testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/labels/" + testLabel.getId()).with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("id").isEqualTo(testLabel.getId()),
                v -> v.node("name").isEqualTo(testLabel.getName())
        );
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/labels").with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        assertThatJson(result.getResponse().getContentAsString()).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        var createData = Map.of("name", "new label");
        var request = post("/api/labels")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createData));

        mockMvc.perform(request).andExpect(status().isCreated());
        Optional<Label> expectedLabel = labelRepository.findByName("new label");
        assertThat(expectedLabel).isPresent();
        assertThat(expectedLabel.get().getCreatedAt()).isNotNull();
    }

    @Test
    public void testUpdate() throws Exception {
        var updateData = new HashMap<String, String>();
        updateData.put("name", "bug");
        var request = put("/api/labels/" + testLabel.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData));

        mockMvc.perform(request).andExpect(status().isOk());
        Optional<Label> expectedLabel = labelRepository.findById(testLabel.getId());
        assertThat(expectedLabel).isPresent();
        assertThat(expectedLabel.get().getName()).isEqualTo("bug");
    }

    @Test
    public void testDelete() throws Exception {
        var request = delete("/api/labels/" + testLabel.getId()).with(jwt());
        mockMvc.perform(request).andExpect(status().isNoContent());
        Optional<Label> expectedEmpty = labelRepository.findById(testLabel.getId());
        assertThat(expectedEmpty).isEmpty();
    }
}
