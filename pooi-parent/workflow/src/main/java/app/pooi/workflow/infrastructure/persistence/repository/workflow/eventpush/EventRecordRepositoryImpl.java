package app.pooi.workflow.infrastructure.persistence.repository.workflow.eventpush;

import app.pooi.workflow.domain.model.workflow.eventpush.EventRecord;
import app.pooi.workflow.domain.repository.EventRecordRepository;
import app.pooi.workflow.infrastructure.persistence.converter.workflow.eventpush.EventRecordConverter;
import app.pooi.workflow.infrastructure.persistence.entity.workflow.eventpush.EventRecordEntity;
import app.pooi.workflow.infrastructure.persistence.service.workflow.eventpush.EventRecordEntityService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

@Repository
class EventRecordRepositoryImpl implements EventRecordRepository {

    @Resource
    private EventRecordEntityService eventRecordService;

    @Resource
    private EventRecordConverter converter;


    @Override
    public boolean saveBatch(Collection<EventRecord> eventRecords, int batchSize) {
        List<EventRecordEntity> entities = eventRecords.stream().map(converter::toEntity).toList();
        return eventRecordService.saveBatch(entities, batchSize);
    }
}
