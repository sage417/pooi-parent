package app.pooi.workflow.infrastructure.persistence.service.impl.workflow.eventpush;

import app.pooi.workflow.infrastructure.persistence.entity.workflow.eventpush.EventRecordEntity;
import app.pooi.workflow.infrastructure.persistence.mapper.workflow.eventpush.EventRecordEntityMapper;
import app.pooi.workflow.infrastructure.persistence.service.workflow.eventpush.EventRecordEntityService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
class EventRecordEntityServiceImpl extends ServiceImpl<EventRecordEntityMapper, EventRecordEntity> implements EventRecordEntityService {

    @Override
    public Page<EventRecordEntity> selectPage(Page<EventRecordEntity> page) {
        return this.baseMapper.selectPage(page, lambdaQuery());
    }
}
