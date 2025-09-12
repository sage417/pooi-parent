package app.pooi.workflow.infrastructure.persistence.service.impl.workflow.eventpush;

import app.pooi.workflow.infrastructure.persistence.entity.workflow.eventpush.EventPushRecordEntity;
import app.pooi.workflow.infrastructure.persistence.mapper.workflow.eventpush.EventPushRecordMapper;
import app.pooi.workflow.infrastructure.persistence.service.workflow.eventpush.EventPushRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class EventPushRecordServiceImpl extends ServiceImpl<EventPushRecordMapper, EventPushRecordEntity> implements EventPushRecordService {
}
