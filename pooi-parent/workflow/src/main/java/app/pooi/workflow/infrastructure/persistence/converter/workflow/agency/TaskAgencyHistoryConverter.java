package app.pooi.workflow.infrastructure.persistence.converter.workflow.agency;

import app.pooi.workflow.domain.model.workflow.agency.TaskAgencyHistory;
import app.pooi.workflow.infrastructure.persistence.entity.workflow.delegate.TaskAgencyHistoryEntity;
import app.pooi.workflow.util.JacksonUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {JacksonUtil.class})
public interface TaskAgencyHistoryConverter {

    @Mapping(target = "delegateDetails", expression = "java(JacksonUtil.writeValueAsString(record.getDelegateDetails()))")
    TaskAgencyHistoryEntity toEntity(final TaskAgencyHistory record);

    @Mapping(target = "delegateDetails", expression = "java(JacksonUtil.readValue(entity.getDelegateDetails(), app.pooi.workflow.domain.model.workflow.agency.TaskApprovalNode.class))")
    TaskAgencyHistory toModel(TaskAgencyHistoryEntity entity);
}
