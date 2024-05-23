package app.pooi.common.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class UtilConfiguration {
    @Bean
    public SpringContextUtil springContextUtil() {
        return new SpringContextUtil();
    }
}
