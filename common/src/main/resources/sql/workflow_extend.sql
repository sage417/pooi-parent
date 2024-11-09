CREATE TABLE `t_workflow_comment`
(
    `id`                    bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `tenant_id`             varchar(255)        NOT NULL DEFAULT '' COMMENT '租户标识',
    `process_definition_id` varchar(255)        NOT NULL DEFAULT '' COMMENT '流程定义id',
    `process_instance_id`   varchar(255)        NOT NULL DEFAULT '' COMMENT '流程实例id',
    `node_id`               varchar(255)        NOT NULL DEFAULT '' COMMENT '节点id',
    `task_id`               varchar(255)        NOT NULL DEFAULT '' COMMENT '任务id',
    `type`                  varchar(255)        NOT NULL DEFAULT '' COMMENT '类型',
    `operator_account`      varchar(255)        NOT NULL DEFAULT '' COMMENT '操作人账号',
    `operation_detail`      json                NULL COMMENT '操作详情',
    `create_time`           datetime(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `update_time`           datetime(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  DEFAULT CHARSET = utf8mb4 COMMENT '流程操作记录';

CREATE TABLE `t_workflow_event_record`
(
    `id`                  bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    event_id              varchar(255)        NOT NULL DEFAULT '' COMMENT '事件id',
    tenant_id             varchar(255)        NOT NULL DEFAULT '' COMMENT '租户标识',
    process_definition_id varchar(255)        NOT NULL DEFAULT '' COMMENT '流程定义id',
    process_instance_id   varchar(255)        NOT NULL DEFAULT '' COMMENT '流程实例id',
    subject_id            varchar(255)        NOT NULL DEFAULT '' COMMENT '租户标识',
    event_type            varchar(127)        NOT NULL DEFAULT '' COMMENT '事件类型',
    event                 json                NULL COMMENT '事件详情',
    `create_time`         datetime(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `update_time`         datetime(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  DEFAULT CHARSET = utf8mb4 COMMENT '事件快照记录';

CREATE TABLE `t_workflow_approval_delegate_config`
(
    `id`                     bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT '租户标识',
    `process_definition_key` varchar(255)        NOT NULL DEFAULT '' COMMENT '流程定义id',
    `type`                   tinyint UNSIGNED    NOT NULL DEFAULT 0 COMMENT '委托类型 0:无效 1:全权委托 2:协助审批',
    `delegate`               varchar(255)        NOT NULL COMMENT '委托人',
    `agents`                 json COMMENT '代理人',
    `valid_time`             datetime(3)         NULL COMMENT '生效时间',
    `invalid_time`           datetime(3)         NULL COMMENT '失效时间',
    `create_time`            datetime(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `update_time`            datetime(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    `is_delete`              tinyint UNSIGNED    NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  DEFAULT CHARSET = utf8mb4 COMMENT '审批委托配置表';

CREATE TABLE `t_workflow_approval_delegate_record`
(
    `id`                     bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT '租户标识',
    `process_definition_key` varchar(255)        NOT NULL DEFAULT '' COMMENT '流程定义id',
    `process_instance_id`    varchar(255)        NOT NULL DEFAULT '' COMMENT '流程实例id',
    `task_id`                varchar(255)        NOT NULL DEFAULT '' COMMENT '任务id',
    `type`                   tinyint UNSIGNED    NOT NULL DEFAULT 0 COMMENT '委托类型 0:无效 1:全权委托 2:协助审批',
    `delegate`               varchar(255)        NOT NULL COMMENT '委托人',
    `agents`                 json COMMENT '代理人',
    `create_time`            datetime(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `update_time`            datetime(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  DEFAULT CHARSET = utf8mb4 COMMENT '审批委托记录表';