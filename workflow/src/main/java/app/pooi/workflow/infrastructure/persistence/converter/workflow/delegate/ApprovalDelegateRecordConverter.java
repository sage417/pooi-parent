package app.pooi.workflow.infrastructure.persistence.converter.workflow.delegate;

import app.pooi.workflow.domain.model.workflow.delegate.ApprovalDelegateRecord;
import app.pooi.workflow.infrastructure.persistence.entity.workflow.delegate.ApprovalDelegateRecordEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ApprovalDelegateRecordConverter {

    ApprovalDelegateRecordEntity toEntity(final ApprovalDelegateRecord record);

    ApprovalDelegateRecord toModel(ApprovalDelegateRecordEntity entity);
}
