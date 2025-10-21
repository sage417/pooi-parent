package app.pooi.workflow.infrastructure.persistence.service.impl.workflow.eventpush;

import app.pooi.workflow.infrastructure.persistence.entity.workflow.eventpush.EventPushRecordEntity;
import app.pooi.workflow.infrastructure.persistence.mapper.workflow.eventpush.EventPushRecordEntityMapper;
import app.pooi.workflow.infrastructure.persistence.service.workflow.eventpush.EventPushRecordEntityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
class EventPushRecordEntityServiceImpl extends ServiceImpl<EventPushRecordEntityMapper, EventPushRecordEntity> implements EventPushRecordEntityService {
}
