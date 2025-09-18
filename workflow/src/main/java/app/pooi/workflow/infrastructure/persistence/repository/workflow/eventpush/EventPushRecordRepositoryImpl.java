package app.pooi.workflow.infrastructure.persistence.repository.workflow.eventpush;

import app.pooi.workflow.infrastructure.persistence.converter.workflow.eventpush.EventPushRecordConverter;
import app.pooi.workflow.infrastructure.persistence.service.workflow.eventpush.EventPushRecordEntityService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class EventPushRecordRepositoryImpl {

    @Resource
    private EventPushRecordEntityService eventPushRecordService;

    @Resource
    private EventPushRecordConverter converter;
}
