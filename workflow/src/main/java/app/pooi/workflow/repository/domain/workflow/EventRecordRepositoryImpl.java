package app.pooi.workflow.repository.domain.workflow;

import app.pooi.workflow.repository.workflow.EventRecordDO;
import app.pooi.workflow.repository.workflow.EventRecordRepository;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
class EventRecordRepositoryImpl extends ServiceImpl<EventRecordMapper, EventRecordDO> implements EventRecordRepository {

    @Override
    public Page<EventRecordDO> selectPage(Page<EventRecordDO> page) {
        return this.baseMapper.selectPage(page, lambdaQuery());
    }
}
