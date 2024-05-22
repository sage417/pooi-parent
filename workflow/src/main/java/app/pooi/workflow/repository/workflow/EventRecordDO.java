package app.pooi.workflow.repository.workflow;

import app.pooi.workflow.constant.EventTypeEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Accessors(chain = true)
@Data
@TableName("t_workflow_event_record")
public class EventRecordDO {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String eventId;

    private String tenantId;

    private String processDefinitionId;

    private String processInstanceId;

    private String subjectId;

    private EventTypeEnum eventType;

    private String event;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
