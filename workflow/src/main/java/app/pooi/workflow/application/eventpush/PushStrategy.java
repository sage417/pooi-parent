package app.pooi.workflow.application.eventpush;

import app.pooi.workflow.infrastructure.persistence.entity.workflow.eventpush.EventRecordEntity;

public interface PushStrategy {

    void push(EventRecordEntity eventRecordDO);
}
