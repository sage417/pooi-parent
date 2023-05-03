package app.pooi.workflow.configuration.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@MapperScan(basePackages = "app.pooi.workflow.repository")
@Configuration
class MybatisConfiguration {
}
