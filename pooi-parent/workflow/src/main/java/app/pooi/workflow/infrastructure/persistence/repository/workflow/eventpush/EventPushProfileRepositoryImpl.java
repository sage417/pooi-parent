package app.pooi.workflow.infrastructure.persistence.repository.workflow.eventpush;

import app.pooi.workflow.domain.model.workflow.eventpush.EventPushProfile;
import app.pooi.workflow.domain.repository.EventPushProfileRepository;
import app.pooi.workflow.infrastructure.persistence.converter.workflow.eventpush.EventPushProfileConverter;
import app.pooi.workflow.infrastructure.persistence.entity.workflow.eventpush.EventPushProfileEntity;
import app.pooi.workflow.infrastructure.persistence.service.workflow.eventpush.EventPushProfileEntityService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Repository
class EventPushProfileRepositoryImpl implements EventPushProfileRepository {

    @Resource
    private EventPushProfileEntityService eventPushProfileService;

    @Resource
    private EventPushProfileConverter converter;

    @Override
    public List<EventPushProfile> findByTenantId(String tenantId) {
        List<EventPushProfileEntity> entities = eventPushProfileService.list(
                Wrappers.lambdaQuery(EventPushProfileEntity.class)
                        .eq(EventPushProfileEntity::getTenantId, tenantId)
        );
        return entities.stream().map(converter::toModel).collect(Collectors.toList());
    }

}
