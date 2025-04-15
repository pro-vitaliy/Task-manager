package hexlet.code.mapper;

import hexlet.code.dto.task.TaskDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {
    private TaskStatusRepository taskStatusRepository;
    private LabelRepository labelRepository;

    @Autowired
    public void setTaskStatusRepository(TaskStatusRepository taskStatusRepository) {
        this.taskStatusRepository = taskStatusRepository;
    }

    @Autowired
    public void setLabelRepository(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    @Mapping(source = "name", target = "title")
    @Mapping(source = "description", target = "content")
    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "taskStatus.slug", target = "status")
    @Mapping(source = "labels", qualifiedByName = "addIdsFromLabels", target = "taskLabelIds")
    public abstract TaskDTO map(Task taskModel);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(source = "assigneeId", target = "assignee")
    @Mapping(source = "status", qualifiedByName = "getTaskStatusFromStatus", target = "taskStatus")
    @Mapping(source = "taskLabelIds", qualifiedByName = "addLabelsById", target = "labels")
    public abstract Task map(TaskDTO taskData);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(source = "status", qualifiedByName = "getTaskStatusFromStatus", target = "taskStatus")
    @Mapping(source = "assigneeId", target = "assignee")
    @Mapping(source = "taskLabelIds", qualifiedByName = "addLabelsById", target = "labels")
    public abstract void update(TaskDTO taskData, @MappingTarget Task taskModel);

    @Named("getTaskStatusFromStatus")
    public TaskStatus getTaskStatusFromStatus(String status) {
        Optional<TaskStatus> taskStatus = taskStatusRepository.findBySlug(status);
        return taskStatus.orElse(null);
    }

    @Named("addLabelsById")
    public Set<Label> addLabelsByName(Set<Long> labelIds) {
        return labelIds.stream()
                .map(labelRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    @Named("addIdsFromLabels")
    public Set<Long> addIdsFromLabels(Set<Label> labels) {
        return labels.stream()
                .map(Label ::getId)
                .collect(Collectors.toSet());
    }
}
