package app.pooi.workflow.configuration.flowable.engine;

import app.pooi.model.workflow.event.ActivityEvent;
import app.pooi.model.workflow.event.WorkFlowEvent;
import app.pooi.workflow.repository.workflow.EventRecordDO;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
public class WorkFlowEventFactory implements InitializingBean {

    private Map<String, Function<EventRecordDO, WorkFlowEvent>> strategies;

    @Override
    public void afterPropertiesSet() {
        this.strategies = ImmutableMap.<String, Function<EventRecordDO, WorkFlowEvent>>builder()
                .put(EventTypeEnum.ACTIVITY_STARTED.getValue(), eventRecordDO ->
                        new ActivityEvent.ActivityStartedEvent().setProcessInstanceId(eventRecordDO.getProcessInstanceId()))
                .put(EventTypeEnum.ACTIVITY_COMPLETED.getValue(), eventRecordDO ->
                        new ActivityEvent.ActivityCompletedEvent().setProcessInstanceId(eventRecordDO.getProcessInstanceId()))
                .build();
    }


    @SuppressWarnings("unchecked")
    public <T extends WorkFlowEvent> T buildEvent(EventRecordDO recordDO) {
        String eventType = recordDO.getEventType();
        Function<EventRecordDO, WorkFlowEvent> function = this.strategies.get(eventType);
        if (function != null) {
            return (T) function.apply(recordDO);
        }
        return null;
    }
}
