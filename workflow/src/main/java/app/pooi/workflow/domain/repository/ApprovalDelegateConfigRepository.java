package app.pooi.workflow.domain.repository;

import app.pooi.workflow.domain.model.workflow.delegate.ApprovalDelegateConfig;

import java.util.List;

public interface ApprovalDelegateConfigRepository {

    List<ApprovalDelegateConfig> selectValidByProcessDefinitionKeyAndTenantId(String definitionKey, String tenantId);
}
