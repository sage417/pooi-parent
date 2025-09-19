package app.pooi.workflow.infrastructure.persistence.repository.workflow.delegate;

import app.pooi.workflow.domain.model.workflow.delegate.ApprovalDelegateConfig;
import app.pooi.workflow.domain.repository.ApprovalDelegateConfigRepository;
import app.pooi.workflow.infrastructure.persistence.converter.workflow.delegate.ApprovalDelegateConfigConverter;
import app.pooi.workflow.infrastructure.persistence.entity.workflow.delegate.ApprovalDelegateConfigEntity;
import app.pooi.workflow.infrastructure.persistence.service.workflow.delegate.ApprovalDelegateConfigEntityService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class ApprovalDelegateConfigRepositoryImpl implements ApprovalDelegateConfigRepository {

    @Resource
    private ApprovalDelegateConfigEntityService approvalDelegateConfigEntityService;

    @Resource
    private ApprovalDelegateConfigConverter converter;

    @Override
    public List<ApprovalDelegateConfig> selectValidByProcessDefinitionKeyAndTenantId(String definitionKey, String tenantId) {
        List<ApprovalDelegateConfigEntity> entities = approvalDelegateConfigEntityService.selectValidByProcessDefinitionKeyAndTenantId(definitionKey, tenantId);
        return entities.stream().map(converter::toModel).toList();
    }
}
