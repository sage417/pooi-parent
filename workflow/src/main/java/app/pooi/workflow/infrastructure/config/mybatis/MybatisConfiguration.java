package app.pooi.workflow.infrastructure.config.mybatis;

import com.baomidou.mybatisplus.autoconfigure.SqlSessionFactoryBeanCustomizer;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;


@MapperScan(basePackages = {"app.pooi.workflow.infrastructure.persistence.mapper"}, annotationClass = Mapper.class, sqlSessionFactoryRef = "sqlSessionFactory")
@Configuration
class MybatisConfiguration {

    @Bean
    SqlSessionFactoryBeanCustomizer sqlSessionFactoryBeanCustomizer() {
        return factoryBean -> {
            factoryBean.setTypeAliases(CollectionUtils.class);
        };
    }
}
