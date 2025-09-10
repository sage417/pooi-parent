package app.pooi.workflow.repository.domain.workflow;

import app.pooi.workflow.repository.workflow.EventPushRecordDO;
import app.pooi.workflow.repository.workflow.EventPushRecordRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class EventPushRecordRepositoryImpl extends ServiceImpl<EventPushRecordMapper, EventPushRecordDO> implements EventPushRecordRepository {
}
