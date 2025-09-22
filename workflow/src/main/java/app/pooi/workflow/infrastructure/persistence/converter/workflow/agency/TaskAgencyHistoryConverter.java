package app.pooi.workflow.infrastructure.persistence.converter.workflow.agency;

import app.pooi.workflow.domain.model.workflow.agency.TaskAgencyHistory;
import app.pooi.workflow.infrastructure.persistence.entity.workflow.delegate.TaskAgencyHistoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskAgencyHistoryConverter {

    TaskAgencyHistoryEntity toEntity(final TaskAgencyHistory record);

    TaskAgencyHistory toModel(TaskAgencyHistoryEntity entity);
}
