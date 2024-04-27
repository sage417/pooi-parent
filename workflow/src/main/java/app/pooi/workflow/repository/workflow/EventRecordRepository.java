package app.pooi.workflow.repository.workflow;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface EventRecordRepository extends IService<EventRecordDO> {

    Page<EventRecordDO> selectPage(Page<EventRecordDO> page);
}
