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
    `operation_detail`       json                NULL COMMENT '操作详情',
    `create_time`           datetime(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `update_time`           datetime(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  DEFAULT CHARSET = utf8mb4 COMMENT '流程操作记录';