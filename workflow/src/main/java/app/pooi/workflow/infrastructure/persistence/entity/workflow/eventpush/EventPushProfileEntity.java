package app.pooi.workflow.infrastructure.persistence.entity.workflow.eventpush;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Accessors(chain = true)
@Data
@TableName("t_workflow_event_push_profile")
public class EventPushProfileEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String tenantId;

    private String type;

    private String profile;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
