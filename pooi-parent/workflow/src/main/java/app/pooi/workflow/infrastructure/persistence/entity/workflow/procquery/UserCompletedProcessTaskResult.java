package app.pooi.workflow.infrastructure.persistence.entity.workflow.procquery;

import lombok.Data;

@Data
public class UserCompletedProcessTaskResult {

    private String processInstanceId;

    private String taskIds;
}
