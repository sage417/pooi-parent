package app.pooi.modules.workflow.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonTypeName(value = "USER_TASK_ASSIGNEE")
@EqualsAndHashCode(callSuper = true)
@Data
public class UserTaskAssigneeEvent extends WorkFlowEvent.TaskBaseEvent {
}
