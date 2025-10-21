package app.pooi.workflow.infrastructure.persistence.converter.workflow.eventpush;

import app.pooi.workflow.domain.model.workflow.eventpush.EventPushRecord;
import app.pooi.workflow.infrastructure.persistence.entity.workflow.eventpush.EventPushRecordEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventPushRecordConverter {

    EventPushRecordEntity toEntity(final EventPushRecord record);

    EventPushRecord toModel(final EventPushRecordEntity entity);
}
