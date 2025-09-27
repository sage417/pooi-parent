package app.pooi.workflow.domain.model.workflow.agency;

import app.pooi.workflow.domain.model.enums.TaskAgencyType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Data
public class TaskAgencyNode {

    private final String currentAssignee;

    private TaskAgencyType agencyType;

    private TaskAgencyNode parent;

    private List<TaskAgencyNode> outgoing;

}
