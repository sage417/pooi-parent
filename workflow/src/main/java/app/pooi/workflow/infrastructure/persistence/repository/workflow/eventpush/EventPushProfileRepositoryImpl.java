package app.pooi.workflow.infrastructure.persistence.repository.workflow.eventpush;

import app.pooi.workflow.domain.model.workflow.eventpush.EventPushProfile;
import app.pooi.workflow.domain.repository.EventPushProfileRepository;
import app.pooi.workflow.infrastructure.persistence.converter.workflow.eventpush.EventPushProfileConverter;
import app.pooi.workflow.infrastructure.persistence.entity.workflow.eventpush.EventPushProfileEntity;
import app.pooi.workflow.infrastructure.persistence.service.workflow.eventpush.EventPushProfileService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class EventPushProfileRepositoryImpl implements EventPushProfileRepository {

    @Resource
    private EventPushProfileService eventPushProfileService;

    @Resource
    private EventPushProfileConverter converter;

    @Override
    public EventPushProfile findByTenantId(String tenantId) {
        EventPushProfileEntity entity = eventPushProfileService.getOne(
                Wrappers.lambdaQuery(EventPushProfileEntity.class)
                        .eq(EventPushProfileEntity::getTenantId, tenantId)
        );
        return converter.toModel(entity);
    }

}
