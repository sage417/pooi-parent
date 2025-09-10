package app.pooi.workflow.repository.domain.workflow;

import app.pooi.workflow.repository.workflow.EventPushProfileDO;
import app.pooi.workflow.repository.workflow.EventPushProfileRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class EventPushProfileRepositoryImpl extends ServiceImpl<EventPushProfileMapper, EventPushProfileDO> implements EventPushProfileRepository {
}
