package app.pooi.workflow.infrastructure.persistence.service.impl.workflow.agency;

import app.pooi.workflow.infrastructure.persistence.entity.workflow.delegate.TaskAgencyHistoryEntity;
import app.pooi.workflow.infrastructure.persistence.mapper.workflow.agency.TaskAgencyHistoryEntityMapper;
import app.pooi.workflow.infrastructure.persistence.service.workflow.agency.TaskAgencyHistoryEntityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class TaskAgentHistoryServiceImpl extends ServiceImpl<TaskAgencyHistoryEntityMapper, TaskAgencyHistoryEntity> implements TaskAgencyHistoryEntityService {
}
