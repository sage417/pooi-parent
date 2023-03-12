package app.pooi.common.dynamicdatasource;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({DynamicDataSourceAutoConfiguration.class})
public @interface EnableDynamicDataSource {

    /**
     * 模块标识
     *
     * @return 模块标识
     */
    String module() default "";
}
