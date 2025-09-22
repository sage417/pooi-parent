package app.pooi.workflow.domain.repository;

import app.pooi.workflow.domain.model.workflow.agency.TaskAgencyProfile;

import java.util.List;

public interface TaskAgencyProfileRepository {

    List<TaskAgencyProfile> selectValidByProcessDefinitionKeyAndTenantId(String definitionKey, String tenantId);
}
