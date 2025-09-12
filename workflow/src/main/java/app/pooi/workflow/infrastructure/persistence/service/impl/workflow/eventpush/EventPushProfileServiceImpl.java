package app.pooi.workflow.infrastructure.persistence.service.impl.workflow.eventpush;

import app.pooi.workflow.infrastructure.persistence.entity.workflow.eventpush.EventPushProfileEntity;
import app.pooi.workflow.infrastructure.persistence.mapper.workflow.eventpush.EventPushProfileMapper;
import app.pooi.workflow.infrastructure.persistence.service.workflow.eventpush.EventPushProfileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class EventPushProfileServiceImpl extends ServiceImpl<EventPushProfileMapper, EventPushProfileEntity> implements EventPushProfileService {
}
