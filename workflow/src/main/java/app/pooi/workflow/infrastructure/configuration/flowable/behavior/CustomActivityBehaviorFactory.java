/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package app.pooi.workflow.infrastructure.configuration.flowable.behavior;

import app.pooi.workflow.application.TaskAgencyApplication;
import app.pooi.workflow.domain.repository.TaskAgencyProfileRepository;
import app.pooi.workflow.infrastructure.configuration.flowable.props.FlowableCustomProperties;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.flowable.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;

import javax.annotation.Resource;

public class CustomActivityBehaviorFactory extends DefaultActivityBehaviorFactory {
    @Resource
    private TaskAgencyProfileRepository approvalDelegateConfigRepository;

    @Resource
    private TaskAgencyApplication taskAgencyApplication;

    @Resource
    private FlowableCustomProperties flowableCustomProperties;

    @Override
    public UserTaskActivityBehavior createUserTaskActivityBehavior(UserTask userTask) {
        return new CustomUserTaskActivityBehavior(userTask, approvalDelegateConfigRepository, taskAgencyApplication, flowableCustomProperties);
    }
}
