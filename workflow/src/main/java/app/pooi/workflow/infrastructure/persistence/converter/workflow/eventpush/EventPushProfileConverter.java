package app.pooi.workflow.infrastructure.persistence.converter.workflow.eventpush;

import app.pooi.workflow.domain.model.workflow.eventpush.EventPushProfile;
import app.pooi.workflow.infrastructure.persistence.entity.workflow.eventpush.EventPushProfileEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventPushProfileConverter {

    EventPushProfileEntity toEntity(final EventPushProfile profile);

    EventPushProfile toModel(final EventPushProfileEntity entity);
}
