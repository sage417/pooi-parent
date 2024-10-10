//package app.pooi.workflow.configuration.flowable.behavior;
//
//import app.pooi.workflow.util.BpmnModelUtil;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.flowable.bpmn.model.FlowNode;
//import org.flowable.bpmn.model.UserTask;
//import org.flowable.common.engine.api.delegate.Expression;
//import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
//import org.flowable.common.engine.api.delegate.event.FlowableEventDispatcher;
//import org.flowable.common.engine.impl.el.ExpressionManager;
//import org.flowable.common.engine.impl.interceptor.CommandContext;
//import org.flowable.common.engine.impl.logging.LoggingSessionConstants;
//import org.flowable.engine.DynamicBpmnConstants;
//import org.flowable.engine.delegate.BpmnError;
//import org.flowable.engine.delegate.DelegateExecution;
//import org.flowable.engine.delegate.TaskListener;
//import org.flowable.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
//import org.flowable.engine.impl.bpmn.helper.DynamicPropertyUtil;
//import org.flowable.engine.impl.bpmn.helper.ErrorPropagation;
//import org.flowable.engine.impl.bpmn.helper.SkipExpressionUtil;
//import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
//import org.flowable.engine.impl.context.BpmnOverrideContext;
//import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
//import org.flowable.engine.impl.util.BpmnLoggingSessionUtil;
//import org.flowable.engine.impl.util.CommandContextUtil;
//import org.flowable.engine.impl.util.TaskHelper;
//import org.flowable.engine.interceptor.CreateUserTaskAfterContext;
//import org.flowable.engine.interceptor.CreateUserTaskBeforeContext;
//import org.flowable.engine.interceptor.MigrationContext;
//import org.flowable.task.service.TaskService;
//import org.flowable.task.service.event.impl.FlowableTaskEventBuilder;
//import org.flowable.task.service.impl.persistence.entity.TaskEntity;
//
//import java.util.List;
//
//@Slf4j
//public class CustomUserTaskActivityBehavior extends UserTaskActivityBehavior {
//    public CustomUserTaskActivityBehavior(UserTask userTask) {
//        super(userTask);
//    }
//
//    @Override
//    public void execute(DelegateExecution execution, MigrationContext migrationContext) {
//        CommandContext commandContext = CommandContextUtil.getCommandContext();
//        ProcessEngineConfigurationImpl processEngineConfiguration = CommandContextUtil.getProcessEngineConfiguration();
//        TaskService taskService = processEngineConfiguration.getTaskServiceConfiguration().getTaskService();
//
//        TaskEntity task = taskService.createTask();
//        task.setExecutionId(execution.getId());
//        task.setTaskDefinitionKey(userTask.getId());
//        task.setPropagatedStageInstanceId(execution.getPropagatedStageInstanceId());
//
//        String activeTaskName = null;
//        String activeTaskDescription = null;
//        String activeTaskDueDate = null;
//        String activeTaskPriority = null;
//        String activeTaskCategory = null;
//        String activeTaskFormKey = null;
//        String activeTaskSkipExpression = null;
//        String activeTaskAssignee = null;
//        String activeTaskOwner = null;
//        String activeTaskIdVariableName = null;
//        List<String> activeTaskCandidateUsers = null;
//        List<String> activeTaskCandidateGroups = null;
//
//        ExpressionManager expressionManager = processEngineConfiguration.getExpressionManager();
//
//        if (processEngineConfiguration.isEnableProcessDefinitionInfoCache()) {
//            ObjectNode taskElementProperties = BpmnOverrideContext.getBpmnOverrideElementProperties(userTask.getId(), execution.getProcessDefinitionId());
//            activeTaskName = DynamicPropertyUtil.getActiveValue(userTask.getName(), DynamicBpmnConstants.USER_TASK_NAME, taskElementProperties);
//            activeTaskDescription = DynamicPropertyUtil.getActiveValue(userTask.getDocumentation(), DynamicBpmnConstants.USER_TASK_DESCRIPTION, taskElementProperties);
//            activeTaskDueDate = DynamicPropertyUtil.getActiveValue(userTask.getDueDate(), DynamicBpmnConstants.USER_TASK_DUEDATE, taskElementProperties);
//            activeTaskPriority = DynamicPropertyUtil.getActiveValue(userTask.getPriority(), DynamicBpmnConstants.USER_TASK_PRIORITY, taskElementProperties);
//            activeTaskCategory = DynamicPropertyUtil.getActiveValue(userTask.getCategory(), DynamicBpmnConstants.USER_TASK_CATEGORY, taskElementProperties);
//            activeTaskFormKey = DynamicPropertyUtil.getActiveValue(userTask.getFormKey(), DynamicBpmnConstants.USER_TASK_FORM_KEY, taskElementProperties);
//            activeTaskSkipExpression = DynamicPropertyUtil.getActiveValue(userTask.getSkipExpression(), DynamicBpmnConstants.TASK_SKIP_EXPRESSION, taskElementProperties);
//            activeTaskAssignee = getAssigneeValue(userTask, migrationContext, taskElementProperties);
//            activeTaskOwner = getOwnerValue(userTask, migrationContext, taskElementProperties);
//            activeTaskCandidateUsers = getActiveValueList(userTask.getCandidateUsers(), DynamicBpmnConstants.USER_TASK_CANDIDATE_USERS, taskElementProperties);
//            activeTaskCandidateGroups = getActiveValueList(userTask.getCandidateGroups(), DynamicBpmnConstants.USER_TASK_CANDIDATE_GROUPS, taskElementProperties);
//            activeTaskIdVariableName = DynamicPropertyUtil.getActiveValue(userTask.getTaskIdVariableName(), DynamicBpmnConstants.USER_TASK_TASK_ID_VARIABLE_NAME, taskElementProperties);
//        } else {
//            activeTaskName = userTask.getName();
//            activeTaskDescription = userTask.getDocumentation();
//            activeTaskDueDate = userTask.getDueDate();
//            activeTaskPriority = userTask.getPriority();
//            activeTaskCategory = userTask.getCategory();
//            activeTaskFormKey = userTask.getFormKey();
//            activeTaskSkipExpression = userTask.getSkipExpression();
//            activeTaskAssignee = getAssigneeValue(userTask, migrationContext, null);
//            activeTaskOwner = getOwnerValue(userTask, migrationContext, null);
//            activeTaskCandidateUsers = userTask.getCandidateUsers();
//            activeTaskCandidateGroups = userTask.getCandidateGroups();
//            activeTaskIdVariableName = userTask.getTaskIdVariableName();
//        }
//
//        CreateUserTaskBeforeContext beforeContext = new CreateUserTaskBeforeContext(userTask, execution, activeTaskName, activeTaskDescription, activeTaskDueDate,
//                activeTaskPriority, activeTaskCategory, activeTaskFormKey, activeTaskSkipExpression, activeTaskAssignee, activeTaskOwner,
//                activeTaskCandidateUsers, activeTaskCandidateGroups);
//
//        if (processEngineConfiguration.getCreateUserTaskInterceptor() != null) {
//            processEngineConfiguration.getCreateUserTaskInterceptor().beforeCreateUserTask(beforeContext);
//        }
//
//        handleName(beforeContext, expressionManager, task, execution);
//        handleDescription(beforeContext, expressionManager, task, execution);
//        handleDueDate(beforeContext, expressionManager, task, execution, processEngineConfiguration, activeTaskDueDate);
//        handlePriority(beforeContext, expressionManager, task, execution, activeTaskPriority);
//        handleCategory(beforeContext, expressionManager, task, execution);
//        handleFormKey(beforeContext, expressionManager, task, execution);
//        // maybe pre handle assignments here to do something
//        preHandleAssignments(beforeContext, expressionManager, null, execution);
//
//        boolean skipUserTask = SkipExpressionUtil.isSkipExpressionEnabled(beforeContext.getSkipExpression(), userTask.getId(), execution, commandContext)
//                && SkipExpressionUtil.shouldSkipFlowElement(beforeContext.getSkipExpression(), userTask.getId(), execution, commandContext);
//
//        TaskHelper.insertTask(task, (ExecutionEntity) execution, !skipUserTask, (!skipUserTask && processEngineConfiguration.isEnableEntityLinks()));
//
//        // Handling assignments need to be done after the task is inserted, to have an id
//        if (!skipUserTask) {
//            if (processEngineConfiguration.isLoggingSessionEnabled()) {
//                BpmnLoggingSessionUtil.addLoggingData(LoggingSessionConstants.TYPE_USER_TASK_CREATE, "User task '" +
//                        task.getName() + "' created", task, execution);
//            }
//
//            handleAssignments(taskService, beforeContext.getAssignee(), beforeContext.getOwner(), beforeContext.getCandidateUsers(),
//                    beforeContext.getCandidateGroups(), task, expressionManager, execution, processEngineConfiguration);
//
//            if (processEngineConfiguration.getCreateUserTaskInterceptor() != null) {
//                CreateUserTaskAfterContext afterContext = new CreateUserTaskAfterContext(userTask, task, execution);
//                processEngineConfiguration.getCreateUserTaskInterceptor().afterCreateUserTask(afterContext);
//            }
//
//            try {
//                processEngineConfiguration.getListenerNotificationHelper().executeTaskListeners(task, TaskListener.EVENTNAME_CREATE);
//            } catch (BpmnError bpmnError) {
//                ErrorPropagation.propagateError(bpmnError, execution);
//                return;
//            }
//
//            // All properties set, now firing 'create' events
//            FlowableEventDispatcher eventDispatcher = processEngineConfiguration.getTaskServiceConfiguration().getEventDispatcher();
//            if (eventDispatcher != null && eventDispatcher.isEnabled()) {
//                eventDispatcher.dispatchEvent(FlowableTaskEventBuilder.createEntityEvent(FlowableEngineEventType.TASK_CREATED, task),
//                        processEngineConfiguration.getEngineCfgKey());
//            }
//
//            if (StringUtils.isNotEmpty(activeTaskIdVariableName)) {
//                Expression expression = expressionManager.createExpression(userTask.getTaskIdVariableName());
//                String idVariableName = (String) expression.getValue(execution);
//                if (StringUtils.isNotEmpty(idVariableName)) {
//                    execution.setVariable(idVariableName, task.getId());
//                }
//            }
//            if (handleIfAutoComplete(task, execution, commandContext)) {
//                TaskHelper.completeTask(task, null, null, null, null, commandContext);
//            }
//        } else {
//            TaskHelper.deleteTask(task, null, false, false, false); // false: no events fired for skipped user task
//            leave(execution);
//        }
//    }
//
//    protected void preHandleAssignments(CreateUserTaskBeforeContext beforeContext, ExpressionManager expressionManager, TaskEntity task, DelegateExecution execution) {
//        if (StringUtils.isNotEmpty(beforeContext.getAssignee())) {
//            Object assigneeExpressionValue = expressionManager.createExpression(beforeContext.getAssignee()).getValue(execution);
//            String assigneeValue = null;
//            if (assigneeExpressionValue != null) {
//                assigneeValue = assigneeExpressionValue.toString();
//            }
//
//            if (StringUtils.isEmpty(assigneeValue)) {
//                // to something when assignee is empty here
//            }
//        }
//    }
//
//    protected boolean handleIfAutoComplete(TaskEntity task, DelegateExecution execution, CommandContext commandContext) {
////        BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(execution.getProcessDefinitionId());
//        BpmnModelUtil.findPreFlowElement(commandContext, ((FlowNode) execution.getCurrentFlowElement()), UserTask.class);
//        return false;
//    }
//}
