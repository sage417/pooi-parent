package app.pooi.workflow.configuration.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@MapperScan(basePackages = "app.pooi.workflow.repository", annotationClass = Mapper.class)
@Configuration
class MybatisConfiguration {
}
