insert into t_tenant_db_info (name, db_code, username, password, url, driver_class, schema_sql)
values ('ds1', 'ds1', 'root', '', 'jdbc:mariadb://nas.pooi.app:13306/workflow-app1', 'org.mariadb.jdbc.Driver',
        'classpath:sql/workflow_ddl.sql');

insert into t_tenant_info (tenant_code, tenant_name)
values ('app1', 'app1');

insert into t_tenant_db_mapping (tenant_code, db_code)
values ('app1', 'ds1');
