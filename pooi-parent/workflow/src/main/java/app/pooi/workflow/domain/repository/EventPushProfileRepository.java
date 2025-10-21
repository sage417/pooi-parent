package app.pooi.workflow.domain.repository;

import app.pooi.workflow.domain.model.workflow.eventpush.EventPushProfile;

import java.util.List;

public interface EventPushProfileRepository {

    List<EventPushProfile> findByTenantId(String tenantId);
}
