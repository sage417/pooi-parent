package com.suchorski.server;

import com.suchorski.server.keycloak.ServerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = LiquibaseAutoConfiguration.class)
@EnableConfigurationProperties(ServerProperties.class)
public class SpringbootKeycloakServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootKeycloakServerApplication.class, args);
	}

}
