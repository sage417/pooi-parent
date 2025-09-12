package app.pooi.workflow.infrastructure.persistence.repository.workflow.eventpush;

import app.pooi.workflow.infrastructure.persistence.converter.workflow.eventpush.EventPushRecordConverter;
import app.pooi.workflow.infrastructure.persistence.service.workflow.eventpush.EventPushRecordService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class EventPushRecordRepositoryImpl {

    @Resource
    private EventPushRecordService eventPushRecordService;

    @Resource
    private EventPushRecordConverter converter;
}
