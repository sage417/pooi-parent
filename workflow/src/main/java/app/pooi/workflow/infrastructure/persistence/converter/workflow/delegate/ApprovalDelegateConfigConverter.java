package app.pooi.workflow.infrastructure.persistence.converter.workflow.delegate;

import app.pooi.workflow.domain.model.workflow.delegate.ApprovalDelegateConfig;
import app.pooi.workflow.infrastructure.persistence.entity.workflow.delegate.ApprovalDelegateConfigEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ApprovalDelegateConfigConverter {

    ApprovalDelegateConfigEntity toEntity(final ApprovalDelegateConfig config);

    ApprovalDelegateConfig toModel(ApprovalDelegateConfigEntity entity);
}
