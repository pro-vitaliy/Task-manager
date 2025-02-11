package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserMapper userMapper;

    private User testUser;
    private JwtRequestPostProcessor testUserToken;

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();

        this.testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);

        testUserToken = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/users/" + testUser.getId()).with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("id").isEqualTo(testUser.getId()),
                v -> v.node("email").isEqualTo(testUser.getEmail()),
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()));
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/users").with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        var userModel = Instancio.of(modelGenerator.getUserModel()).create();
        var userData = new UserDTO(JsonNullable.of(userModel.getEmail()), JsonNullable.of(userModel.getPassword()));
        userData.setFirstName(JsonNullable.of(userModel.getFirstName()));
        userData.setLastName(JsonNullable.of(userModel.getLastName()));

        var request = post("/api/users")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userData));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        Optional<User> expectedUser = userRepository.findByEmail(userData.getEmail().get());
        assertThat(expectedUser)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getEmail()).isEqualTo(userData.getEmail().get());
                    assertThat(user.getFirstName()).isEqualTo(userData.getFirstName().get());
                    assertThat(user.getLastName()).isEqualTo(userData.getLastName().get());
                    assertThat(user.getCreatedAt()).isNotNull();
                });
    }

    @Test
    public void testUpdate() throws Exception {
        var data = new HashMap<String, String>();
        data.put("lastName", "Soprano");

        var request = put("/api/users/" + testUser.getId())
                .with(testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        Optional<User> expectedUser = userRepository.findById(testUser.getId());

        assertThat(expectedUser).isPresent();
        assertThat(expectedUser.get().getLastName()).isEqualTo("Soprano");
    }

    @Test
    public void testDelete() throws Exception {
        var request = delete("/api/users/" + testUser.getId()).with(testUserToken);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        Optional<User> expected = userRepository.findById(testUser.getId());
        assertThat(expected).isEmpty();
    }
}
