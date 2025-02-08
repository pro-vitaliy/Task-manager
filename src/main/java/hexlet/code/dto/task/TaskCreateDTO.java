package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class TaskCreateDTO {
    private int index;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    @NotBlank
    private String title;

    private String content;

    @NotNull
    private String status;

    private Set<Long> taskLabelIds = new HashSet<>();

    public TaskCreateDTO(String status, String title) {
        this.status = status;
        this.title = title;
    }
}
