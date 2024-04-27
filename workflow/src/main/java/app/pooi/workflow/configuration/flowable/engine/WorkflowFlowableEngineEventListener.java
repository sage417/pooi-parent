package app.pooi.workflow.configuration.flowable.engine;

import app.pooi.workflow.repository.workflow.EventRecordRepository;
import lombok.Setter;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.service.TaskService;

import javax.annotation.Resource;

@Setter
public class WorkflowFlowableEngineEventListener extends AbstractFlowableEngineEventListener {

    @Resource
    private EventRecordRepository eventRecordRepository;

    @Override
    protected void taskCompleted(FlowableEngineEntityEvent event) {
        TaskService taskService = CommandContextUtil.getTaskService();
        super.taskCompleted(event);
    }
}
