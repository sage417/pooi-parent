package app.pooi.workflow.infrastructure.persistence.service.impl.workflow.eventpush;

import app.pooi.workflow.infrastructure.persistence.entity.workflow.eventpush.EventPushProfileEntity;
import app.pooi.workflow.infrastructure.persistence.mapper.workflow.eventpush.EventPushProfileEntityMapper;
import app.pooi.workflow.infrastructure.persistence.service.workflow.eventpush.EventPushProfileEntityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class EventPushProfileEntityServiceImpl extends ServiceImpl<EventPushProfileEntityMapper, EventPushProfileEntity> implements EventPushProfileEntityService {
}
