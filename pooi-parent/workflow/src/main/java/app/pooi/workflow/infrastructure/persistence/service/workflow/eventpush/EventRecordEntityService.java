package app.pooi.workflow.infrastructure.persistence.service.workflow.eventpush;

import app.pooi.workflow.infrastructure.persistence.entity.workflow.eventpush.EventRecordEntity;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface EventRecordEntityService extends IService<EventRecordEntity> {

    Page<EventRecordEntity> selectPage(Page<EventRecordEntity> page);
}
