insert into t_tenant_db_info (name, db_code, username, password, url, driver_class)
values ('ds1', 'ds1', 'sa', '', 'jdbc:h2:mem:pooi-workflow-app1;INIT=RUNSCRIPT FROM ''classpath:sql/workflow_extend.sql''', 'org.h2.Driver');

insert into t_tenant_info (tenant_code, tenant_name)
values ('app1', 'app1');

insert into t_tenant_db_mapping (tenant_code, db_code)
values ('app1', 'ds1');
