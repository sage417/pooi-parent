package app.pooi.workflow.domain.repository;

import app.pooi.workflow.domain.model.workflow.eventpush.EventPushProfile;

public interface EventPushProfileRepository {

    EventPushProfile findByTenantId(String tenantId);
}
