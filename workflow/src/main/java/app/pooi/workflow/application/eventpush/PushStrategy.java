package app.pooi.workflow.application.eventpush;

import app.pooi.workflow.domain.model.workflow.eventpush.EventPushProfile;
import app.pooi.workflow.domain.model.workflow.eventpush.EventRecord;

public interface PushStrategy {

    void push(EventPushProfile profile, EventRecord eventRecordDO);
}
