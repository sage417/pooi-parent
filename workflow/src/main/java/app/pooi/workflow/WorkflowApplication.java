package app.pooi.workflow;

import app.pooi.tenant.dynamicdatasource.EnableDynamicDataSource;
import org.dromara.dynamictp.core.spring.EnableDynamicTp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDynamicTp
@EnableDynamicDataSource
@SpringBootApplication
public class WorkflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkflowApplication.class, args);
    }

}
