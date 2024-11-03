/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package app.pooi.workflow.configuration.flowable.behavior;

import app.pooi.workflow.configuration.flowable.props.FlowableCustomProperties;
import app.pooi.workflow.repository.workflow.ApprovalDelegateConfigRepository;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.flowable.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;

import javax.annotation.Resource;

public class CustomActivityBehaviorFactory extends DefaultActivityBehaviorFactory {

    @Resource
    private ApprovalDelegateConfigRepository approvalDelegateConfigRepository;

    @Resource
    private FlowableCustomProperties flowableCustomProperties;

    @Override
    public UserTaskActivityBehavior createUserTaskActivityBehavior(UserTask userTask) {
        return new CustomUserTaskActivityBehavior(userTask, approvalDelegateConfigRepository, flowableCustomProperties);
    }
}
