CREATE TABLE `t_tenant_db_info` (
                                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                         `name` varchar(100) NOT NULL COMMENT '数据源名',
                                         `db_code` varchar(100) NOT NULL COMMENT '数据库标识',
                                         `username` varchar(100) NOT NULL COMMENT '数据源账号',
                                         `password` varchar(500) NOT NULL COMMENT '数据源密码',
                                         `url` varchar(500) NOT NULL COMMENT '连接地址',
                                         `driver_class` varchar(100) NOT NULL COMMENT '驱动类',
                                         `type` varchar(50) NOT NULL COMMENT '数据源类型',
                                         `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
                                         `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
                                         `module` varchar(50) NOT NULL DEFAULT '' COMMENT '数据源所属子模块',
                                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='数据源信息';
