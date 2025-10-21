package app.pooi.workflow.infrastructure.persistence.converter.workflow.eventpush;

import app.pooi.workflow.domain.model.workflow.eventpush.EventRecord;
import app.pooi.workflow.infrastructure.persistence.entity.workflow.eventpush.EventRecordEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventRecordConverter {

    EventRecordEntity toEntity(final EventRecord record);

    EventRecord toModel(final EventRecordEntity entity);
}
