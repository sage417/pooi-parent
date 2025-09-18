package app.pooi.workflow.infrastructure.persistence.repository.workflow.eventpush;

import app.pooi.workflow.infrastructure.persistence.converter.workflow.eventpush.EventRecordConverter;
import app.pooi.workflow.infrastructure.persistence.service.workflow.eventpush.EventRecordEntityService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class EventRecordRepositoryImpl {

    @Resource
    private EventRecordEntityService eventRecordService;

    @Resource
    private EventRecordConverter converter;
}
