package app.pooi.workflow.application.eventpush;

import app.pooi.workflow.repository.workflow.EventRecordDO;

public interface PushStrategy {

    void push(EventRecordDO eventRecordDO);
}
