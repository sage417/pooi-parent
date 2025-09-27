package app.pooi.workflow.application.service;

import app.pooi.workflow.domain.model.workflow.agency.TaskApprovalNode;
import app.pooi.workflow.domain.model.workflow.agency.TaskDelegateNode;
import app.pooi.workflow.util.TravelNode;
import com.google.common.collect.Lists;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskAgencyApplicationTest {

    @Test
    void calculateApprovalDelegateRelation() {
        TaskApprovalNode taskApprovalNode = TaskApprovalNode.newApproval();
        taskApprovalNode.addChild(new TaskApprovalNode("A"));
        taskApprovalNode.addChild(new TaskApprovalNode("B"));
        taskApprovalNode.addChild(new TaskApprovalNode("C"));

        TaskDelegateNode taskDelegateNode = new TaskDelegateNode("__DELEGATE__");
        TaskDelegateNode delegateNodeA = new TaskDelegateNode("A");
        delegateNodeA.addChild(new TaskDelegateNode("E"));
        delegateNodeA.addChild(new TaskDelegateNode("F"));
        taskDelegateNode.addChild(delegateNodeA);
        TaskDelegateNode delegateNodeB = new TaskDelegateNode("B");
        TaskDelegateNode delegateNodeC = new TaskDelegateNode("C");
        delegateNodeB.addChild(delegateNodeC);
        taskDelegateNode.addChild(delegateNodeB);
        delegateNodeC.addChild(new TaskDelegateNode("D"));
        taskDelegateNode.addChild(delegateNodeC);


        TaskAgencyAppService taskAgencyAppService = new TaskAgencyAppService();
        TaskApprovalNode approvalNode = taskAgencyAppService.calculateApprovalDelegateRelation(taskApprovalNode, taskDelegateNode);
        // D E F
        Assertions.assertThat(approvalNode.getChildren()).hasSize(3);
        Assertions.assertThat(approvalNode.getChildren().stream().map(TravelNode::getValue).toList())
                .containsAll(Lists.newArrayList("D", "E", "F"));

        approvalNode.getChildren().stream().map(TravelNode::getValue).toList();
    }
}