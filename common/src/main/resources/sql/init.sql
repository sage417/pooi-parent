CREATE TABLE `t_tenant_db_info`
(
    `id`           bigint(20)   NOT NULL AUTO_INCREMENT,
    `name`         varchar(100) NOT NULL DEFAULT '' COMMENT '数据源名',
    `db_code`      varchar(100) NOT NULL DEFAULT '' COMMENT '数据库标识',
    `username`     varchar(100) NOT NULL DEFAULT '' COMMENT '数据源账号',
    `password`     varchar(500) NOT NULL DEFAULT '' COMMENT '数据源密码',
    `url`          varchar(500) NOT NULL DEFAULT '' COMMENT '连接地址',
    `driver_class` varchar(100) NOT NULL DEFAULT '' COMMENT '驱动类',
    `type`         varchar(50)  NOT NULL DEFAULT '' COMMENT '数据源类型',
    `create_time`  datetime(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `update_time`  datetime(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    `module`       varchar(50)  NOT NULL DEFAULT '' COMMENT '数据源所属子模块',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  DEFAULT CHARSET = utf8mb4 COMMENT ='数据源信息';

CREATE TABLE `t_tenant_info`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT,
    `tenant_code` varchar(100) NOT NULL DEFAULT '' COMMENT '租户标识',
    `tenant_name` varchar(100) not null DEFAULT '' comment '租户名称',
    `create_time` datetime(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `update_time` datetime(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
);

CREATE TABLE `t_tenant_db_mapping`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT,
    `tenant_code` varchar(100) NOT NULL DEFAULT '' COMMENT '租户标识',
    `db_code`     varchar(100) NOT NULL DEFAULT '' COMMENT '数据源标识',
    `type`        varchar(50)  NOT NULL DEFAULT '' COMMENT '数据源类型',
    `module`      varchar(50)  NOT NULL DEFAULT '' COMMENT '数据源所属子模块',
    `create_time` datetime(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `update_time` datetime(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    PRIMARY KEY (`id`),
    KEY `idx_tenant_code` (`tenant_code`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  DEFAULT CHARSET = utf8mb4 COMMENT ='租户和数据源映射表'

