package app.pooi.workflow.infrastructure.persistence.converter.workflow.eventpush;

import app.pooi.basic.util.IEnum;
import app.pooi.workflow.domain.model.enums.EventPushType;
import app.pooi.workflow.domain.model.workflow.eventpush.EventPushProfile;
import app.pooi.workflow.infrastructure.persistence.entity.workflow.eventpush.EventPushProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {IEnum.class, EventPushType.class})
public interface EventPushProfileConverter {

    @Mapping(target = "type", expression = "java(profile.getType().getValue())")
    EventPushProfileEntity toEntity(final EventPushProfile profile);

    @Mapping(target = "type", expression = "java(IEnum.fromValue(EventPushType.class, entity.getType()))")
    EventPushProfile toModel(final EventPushProfileEntity entity);
}
