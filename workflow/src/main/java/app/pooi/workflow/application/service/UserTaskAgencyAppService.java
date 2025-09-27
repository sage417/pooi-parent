package app.pooi.workflow.application.service;

import app.pooi.workflow.domain.model.enums.TaskAgencyType;
import app.pooi.workflow.domain.model.workflow.agency.*;
import app.pooi.workflow.domain.repository.TaskAgencyProfileRepository;
import app.pooi.workflow.util.TravelNode;
import com.google.common.collect.Sets;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserTaskAgencyAppService {

    @Resource
    private TaskAgencyProfileRepository taskAgencyProfileRepository;


    public TaskDelegateResult matchTaskDelegate(ExecutionEntity execution, Set<String> taskAssignees, String tenantId) {

        List<TaskAgencyProfile> agencyProfiles = this.taskAgencyProfileRepository
                .selectValidByProcessDefinitionKeyAndTenantId(execution.getProcessDefinitionKey(), tenantId);

        if (CollectionUtils.isEmpty(agencyProfiles)) {
            return TaskDelegateResult.NO_NEED_CHANGE_ASSIGNEE_RESULT;
        }

        // 1) flowable calculate candidates
        TaskApprovalNode taskApprovalRelation = generateTaskApprovalNode(taskAssignees);
        // 2) agency profile tree building
        TaskDelegateNode taskDelegateRelation = generateTaskDelegateRelation(agencyProfiles);
        // 3) replace candidates after delegate and search delegate path
        TaskApprovalNode taskApprovalRelationAfterDelegate = calculateApprovalDelegateRelation(taskApprovalRelation, taskDelegateRelation);

        Set<String> assigneeAfterDelegate = taskApprovalRelationAfterDelegate.getChildren().stream()
                .map(TravelNode::getValue).collect(Collectors.toSet());

        return new TaskDelegateResult().setMatchDelegateProfile(Sets.difference(taskAssignees, assigneeAfterDelegate).isEmpty())
                .setAssigneeAfterDelegate(taskApprovalRelationAfterDelegate);
    }

    public TaskApprovalNode generateTaskApprovalNode(@NonNull Set<String> candidates) {
        TaskApprovalNode approvalNode = TaskApprovalNode.newApproval();
        for (String candidate : candidates) {
            approvalNode.addChild(new TaskApprovalNode(candidate));
        }
        return approvalNode;
    }

    TaskApprovalNode calculateApprovalDelegateRelation(@NonNull TaskApprovalNode approvalNode, @NonNull TaskDelegateNode taskDelegateNode) {
        TaskApprovalNode newApprovalNode = TaskApprovalNode.newApproval();

        for (TaskApprovalNode child : approvalNode.getChildren()) {
            Optional<TaskDelegateNode> optionalTaskDelegateNode = taskDelegateNode.getChildren().stream()
                    .filter(delegateNode -> delegateNode.getValue().equals(child.getValue()))
                    .findFirst();
            if (optionalTaskDelegateNode.isEmpty()) {
                newApprovalNode.addChild(new TaskApprovalNode(child.getValue()));
                continue;
            }
            // delegate root -> approval
            TaskDelegateNode matchDelegateNode = optionalTaskDelegateNode.get();
            // delegate root -> approval -> leaf node
            List<List<TaskDelegateNode>> leafNodePaths = matchDelegateNode.getLeafNodes();

            for (List<TaskDelegateNode> leafNodePath : leafNodePaths) {
                //
                Optional<TaskApprovalNode> taskApprovalNode = newApprovalNode.getChildren().stream()
                        .filter(node -> node.getValue().equals(leafNodePath.getLast().getValue()))
                        .findFirst();
                if (taskApprovalNode.isPresent()) {
                    taskApprovalNode.get().getDelegateChains().add(new TaskDelegatePath(leafNodePath));
                } else {
                    newApprovalNode.addChild(TaskApprovalNode.fromDelegateNodePath(leafNodePath));
                }
            }
        }

        return newApprovalNode;
    }


    private TaskDelegateNode generateTaskDelegateRelation(List<TaskAgencyProfile> taskAgencyProfiles) {
        TaskDelegateNode rootNode = new TaskDelegateNode("__DELEGATE__");
        // args check
        if (CollectionUtils.isEmpty(taskAgencyProfiles)) {
            return rootNode;
        }

        taskAgencyProfiles = taskAgencyProfiles.stream()
                .filter(profile -> TaskAgencyType.DELEGATE.equals(profile.getAgencyType())).toList();

        for (TaskAgencyProfile taskAgencyProfile : taskAgencyProfiles) {
            TaskDelegateNode delegateNode = TravelNode.find(rootNode, taskAgencyProfile.getDelegator())
                    .orElseGet(() -> new TaskDelegateNode(taskAgencyProfile.getDelegator()));
            rootNode.addChild(delegateNode);
            // process delegatee
            for (String delegatee : taskAgencyProfile.getDelegatee()) {
                TaskDelegateNode delegatteNode = TravelNode.find(rootNode, delegatee).orElseGet(() -> new TaskDelegateNode(delegatee));
                delegateNode.addChild(delegatteNode);
            }
        }
        return rootNode;
    }

}
