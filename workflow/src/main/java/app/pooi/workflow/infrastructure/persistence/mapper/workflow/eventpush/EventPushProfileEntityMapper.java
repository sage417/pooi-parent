package app.pooi.workflow.infrastructure.persistence.mapper.workflow.eventpush;

import app.pooi.workflow.infrastructure.persistence.entity.workflow.eventpush.EventPushProfileEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface EventPushProfileEntityMapper extends BaseMapper<EventPushProfileEntity> {
}
