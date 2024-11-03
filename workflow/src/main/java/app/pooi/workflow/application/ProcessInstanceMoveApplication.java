package app.pooi.workflow.application;

import app.pooi.workflow.applicationsupport.workflowcomment.AddCommentBO;
import app.pooi.workflow.applicationsupport.workflowcomment.CommentSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.Execution;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ProcessInstanceMoveApplication {

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private CommentSupport commentSupport;


    public void rollback(String processInstanceId, String sourceActivityId, String targetActivityId) {

        List<Execution> currentExecutions = runtimeService.createExecutionQuery()
                .processInstanceId(processInstanceId)
                .onlyChildExecutions()
                .list();

        Optional<Execution> optSourceExecution = currentExecutions.stream().filter(execution -> StringUtils.equals(execution.getActivityId(), sourceActivityId))
                .findFirst();
        if (optSourceExecution.isEmpty()) {
            log.warn("sourceActivityId: {} not in current child executions", sourceActivityId);
            return;
        }

        List<String> executionIds = currentExecutions.stream().map(Execution::getId).collect(Collectors.toList());

        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(processInstanceId)
                .moveExecutionsToSingleActivityId(executionIds, targetActivityId)
                .changeState();

        AddCommentBO addCommentBO = commentSupport.createFormExecution(optSourceExecution.get());
        addCommentBO.setType("ROLLBACK");
        commentSupport.recordComment(addCommentBO);

    }

}
