package app.pooi.workflow.domain.model.workflow.agency;

import app.pooi.workflow.domain.model.enums.TaskAgencyType;
import app.pooi.workflow.util.TravelNode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TaskApprovalNode extends TravelNode<TaskApprovalNode> {

    private final TaskAgencyType taskAgencyType;

    private List<TaskDelegatePath> delegateChains = new ArrayList<>(0);

    public TaskApprovalNode(String value) {
        super(value);
        this.taskAgencyType = TaskAgencyType.NONE;
    }

    private TaskApprovalNode(String value, TaskAgencyType taskAgencyType) {
        super(value);
        this.taskAgencyType = taskAgencyType;
    }

    public static TaskApprovalNode newApproval() {
        return new TaskApprovalNode("__APPROVAL__");
    }

    public static TaskApprovalNode fromDelegateNodePath(List<TaskDelegateNode> leafNodePath) {
        TaskApprovalNode taskApprovalNode = new TaskApprovalNode(leafNodePath.getLast().getValue(), TaskAgencyType.DELEGATE);
        taskApprovalNode.getDelegateChains().add(new TaskDelegatePath(leafNodePath));
        return taskApprovalNode;
    }


}
