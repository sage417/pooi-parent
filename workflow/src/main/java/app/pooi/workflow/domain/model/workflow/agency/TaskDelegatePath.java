package app.pooi.workflow.domain.model.workflow.agency;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TaskDelegatePath {

    private List<TaskDelegateNode> delegateChains;

    public TaskDelegatePath(List<TaskDelegateNode> delegateChains) {
        this.delegateChains = new ArrayList<>(delegateChains);
    }
}
