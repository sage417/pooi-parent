package app.pooi.workflow.infrastructure.persistence.service.impl.workflow.eventpush;

import app.pooi.workflow.infrastructure.persistence.entity.workflow.eventpush.EventRecordEntity;
import app.pooi.workflow.infrastructure.persistence.mapper.workflow.eventpush.EventRecordMapper;
import app.pooi.workflow.infrastructure.persistence.service.workflow.eventpush.EventRecordService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class EventRecordServiceImpl extends ServiceImpl<EventRecordMapper, EventRecordEntity> implements EventRecordService {

    @Override
    public Page<EventRecordEntity> selectPage(Page<EventRecordEntity> page) {
        return this.baseMapper.selectPage(page, lambdaQuery());
    }
}
