package app.pooi.workflow.infrastructure.persistence.repository.workflow.agency;

import app.pooi.workflow.domain.model.workflow.agency.TaskAgencyHistory;
import app.pooi.workflow.domain.repository.TaskAgencyHistoryRepository;
import app.pooi.workflow.infrastructure.persistence.converter.workflow.agency.TaskAgencyHistoryConverter;
import app.pooi.workflow.infrastructure.persistence.service.workflow.agency.TaskAgencyHistoryEntityService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class TaskAgencyHistoryRepositoryImpl implements TaskAgencyHistoryRepository {

    @Resource
    private TaskAgencyHistoryEntityService approvalDelegateRecordEntityService;

    @Resource
    private TaskAgencyHistoryConverter converter;


    @Override
    public void save(TaskAgencyHistory taskAgencyHistory) {
        this.approvalDelegateRecordEntityService.save(converter.toEntity(taskAgencyHistory));
    }

}
