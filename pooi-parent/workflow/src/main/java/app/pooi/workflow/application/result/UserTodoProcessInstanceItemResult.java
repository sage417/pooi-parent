package app.pooi.workflow.application.result;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserTodoProcessInstanceItemResult {

    private String taskId;

    private String processInstanceId;

    private String processInstanceName;

    private LocalDateTime processInstanceStartTime;

    private String currentNodeNames;

}