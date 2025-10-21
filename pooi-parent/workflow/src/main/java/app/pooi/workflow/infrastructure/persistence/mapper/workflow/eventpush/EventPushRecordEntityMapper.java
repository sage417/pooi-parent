package app.pooi.workflow.infrastructure.persistence.mapper.workflow.eventpush;

import app.pooi.workflow.infrastructure.persistence.entity.workflow.eventpush.EventPushRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface EventPushRecordEntityMapper extends BaseMapper<EventPushRecordEntity> {
}
