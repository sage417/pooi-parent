package app.pooi.workflow.domain.model.workflow.comment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {

    private String tenantId;

    private String processDefinitionId;

    private String processInstanceId;

    private String nodeId;

    private String taskId;

    private String type;

    private String operatorAccount;

    private String operationDetail;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
