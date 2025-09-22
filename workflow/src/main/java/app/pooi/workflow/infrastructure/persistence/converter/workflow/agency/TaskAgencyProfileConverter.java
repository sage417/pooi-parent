package app.pooi.workflow.infrastructure.persistence.converter.workflow.agency;

import app.pooi.workflow.domain.model.workflow.agency.TaskAgencyProfile;
import app.pooi.workflow.infrastructure.persistence.entity.workflow.delegate.TaskAgencyProfileEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskAgencyProfileConverter {

    TaskAgencyProfileEntity toEntity(final TaskAgencyProfile config);

    TaskAgencyProfile toModel(TaskAgencyProfileEntity entity);
}
