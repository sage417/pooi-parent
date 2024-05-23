CREATE TABLE `t_workflow_event_record`
(
    `id`                  identity, -- bigint(20)   NOT NULL AUTO_INCREMENT,
    event_id              varchar(100) NOT NULL DEFAULT '' COMMENT '租户标识',
    tenant_id             varchar(100) NOT NULL DEFAULT '' COMMENT '租户标识',
    process_definition_id varchar(255) NOT NULL DEFAULT '' COMMENT '流程定义id',
    process_instance_id   varchar(255) NOT NULL DEFAULT '' COMMENT '流程实例id',
    subject_id            varchar(255) NOT NULL DEFAULT '' COMMENT '租户标识',
    event_type            varchar(100) NOT NULL DEFAULT '' COMMENT '事件类型',
    event                 json         NULL COMMENT '事件详情',
    `create_time`         datetime(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `update_time`         datetime(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    PRIMARY KEY (`id`)
);
--     ENGINE = InnoDB
--     AUTO_INCREMENT = 0
--     DEFAULT CHARSET = utf8mb4 COMMENT ='事件快照记录';