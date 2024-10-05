package app.pooi.model.workflow.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonTypeName(value = "USER_TASK_COMPLETED")
@EqualsAndHashCode(callSuper = true)
@Data
public class UserTaskCompletedEvent extends WorkFlowEvent.TaskBaseEvent {
}
