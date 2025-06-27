package com.suchorski.server;

import com.suchorski.server.keycloak.KeycloakServerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = LiquibaseAutoConfiguration.class)
@EnableConfigurationProperties(KeycloakServerProperties.class)
public class KeycloakServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeycloakServerApplication.class, args);
	}

}
