package app.pooi.workflow.applicationsupport.workflowcomment;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class AddCommentBO {

    private String tenantId;

    private String processDefinitionId;

    private String processInstanceId;

    private String nodeId;

    private String taskId;

    private String type;

    private String operatorAccount;

}
