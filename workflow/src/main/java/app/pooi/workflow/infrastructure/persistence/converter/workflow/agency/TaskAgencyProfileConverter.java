package app.pooi.workflow.infrastructure.persistence.converter.workflow.agency;

import app.pooi.basic.util.IEnum;
import app.pooi.workflow.domain.model.enums.TaskAgencyType;
import app.pooi.workflow.domain.model.workflow.agency.TaskAgencyProfile;
import app.pooi.workflow.infrastructure.persistence.entity.workflow.delegate.TaskAgencyProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {IEnum.class, TaskAgencyType.class})
public interface TaskAgencyProfileConverter {

    @Mapping(target = "type", source = "config.agencyType.value")
    TaskAgencyProfileEntity toEntity(final TaskAgencyProfile config);

    @Mapping(target = "agencyType", expression = "java(IEnum.fromValue(TaskAgencyType.class, entity.getType()))")
    TaskAgencyProfile toModel(TaskAgencyProfileEntity entity);
}
