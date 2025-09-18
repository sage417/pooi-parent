package app.pooi.workflow.application.eventpush.strategy;

import app.pooi.workflow.application.eventpush.PushStrategy;
import app.pooi.workflow.domain.model.workflow.eventpush.EventPushProfile;
import app.pooi.workflow.infrastructure.persistence.entity.workflow.eventpush.EventRecordEntity;
import org.springframework.stereotype.Component;


@Component
public class MQPushStrategy implements PushStrategy {


    @Override
    public void push(EventPushProfile profile, EventRecordEntity eventRecordDO) {

    }
}
