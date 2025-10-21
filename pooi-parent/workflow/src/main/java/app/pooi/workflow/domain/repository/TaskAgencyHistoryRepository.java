package app.pooi.workflow.domain.repository;

import app.pooi.workflow.domain.model.workflow.agency.TaskAgencyHistory;

public interface TaskAgencyHistoryRepository {

    void save(TaskAgencyHistory taskAgencyHistory);
}
