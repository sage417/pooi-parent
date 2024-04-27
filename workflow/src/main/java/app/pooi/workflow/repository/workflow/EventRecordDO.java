package app.pooi.workflow.repository.workflow;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_workflow_event_record")
public class EventRecordDO {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String eventId;

    private String tenantId;

    private String processInstanceId;

    private String eventType;

    private String event;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
