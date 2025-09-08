package app.pooi.workflow.application.eventpush;

import app.pooi.workflow.repository.workflow.EventRecordDO;
import org.springframework.stereotype.Component;

@Component
public class HttpPushStrategy implements PushStrategy {

    @Override
    public void push(EventRecordDO eventRecordDO) {

    }
}
