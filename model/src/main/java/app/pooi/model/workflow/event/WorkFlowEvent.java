package app.pooi.model.workflow.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

@Data
@Accessors(chain = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "eventType", visible = false, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(InstanceStartedEvent.class),
        @JsonSubTypes.Type(InstanceCompletedEvent.class),
        @JsonSubTypes.Type(ActivityStartedEvent.class),
        @JsonSubTypes.Type(ActivityCompletedEvent.class),
        @JsonSubTypes.Type(UserTaskCreatedEvent.class),
        @JsonSubTypes.Type(UserTaskAssigneeEvent.class),
        @JsonSubTypes.Type(UserTaskCompletedEvent.class),
})
public class WorkFlowEvent implements Serializable {

    private String processInstanceId;

    private String processDefinitionId;

    private String processDefinitionKey;

    private String processDefinitionVersion;

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(chain = true)
    public static class InstanceBaseEvent extends WorkFlowEvent {

        private Map<String, Object> variables;

    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(chain = true)
    public static class ActivityBaseEvent extends WorkFlowEvent {

        private String activityId;

    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(chain = true)
    public static class TaskBaseEvent extends WorkFlowEvent {

        private String taskId;

    }

}
