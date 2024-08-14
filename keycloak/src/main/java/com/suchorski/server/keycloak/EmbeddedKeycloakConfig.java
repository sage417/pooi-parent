package com.suchorski.server.keycloak;

import com.suchorski.server.keycloak.providers.SimplePlatformProvider;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.keycloak.platform.Platform;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.naming.*;
import javax.naming.spi.NamingManager;
import javax.sql.DataSource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
@RequiredArgsConstructor
public class EmbeddedKeycloakConfig {

	private final KeycloakServerProperties properties;
	private final DataSource dataSource;

	@Bean
	ServletRegistrationBean<HttpServlet30Dispatcher> keycloakJaxRsApplication() {
		try {
			mockJndiEnvironment();
		} catch (NamingException ex) {
			Logger.getLogger(EmbeddedKeycloakConfig.class.getName()).log(Level.SEVERE, null, ex);
		}
		EmbeddedKeycloakApplication.properties = properties;
		final var servlet = new ServletRegistrationBean<HttpServlet30Dispatcher>(new HttpServlet30Dispatcher());
		servlet.addInitParameter("jakarta.ws.rs.Application", EmbeddedKeycloakApplication.class.getName());
		servlet.addInitParameter(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX, properties.contextPath());
		servlet.addInitParameter(ResteasyContextParameters.RESTEASY_USE_CONTAINER_FORM_PARAMS, "true");
		servlet.addUrlMappings(properties.contextPath() + "/*");
		servlet.setLoadOnStartup(2);
		servlet.setAsyncSupported(true);
		return servlet;
	}

	@Bean
	FilterRegistrationBean<EmbeddedKeycloakRequestFilter> keycloakSessionManagement() {
		final var filter = new FilterRegistrationBean<EmbeddedKeycloakRequestFilter>();
		filter.setName("Keycloak Session Management");
		filter.setFilter(new EmbeddedKeycloakRequestFilter());
		filter.addUrlPatterns(properties.contextPath() + "/*");
		return filter;
	}

	private void mockJndiEnvironment() throws NamingException {
		NamingManager.setInitialContextFactoryBuilder((env) -> (environment) -> new InitialContext() {
			@Override
			public Object lookup(Name name) {
				return lookup(name.toString());
			}

			@Override
			public Object lookup(String name) {
				if ("spring/datasource".equals(name)) {
					return dataSource;
				} else if (name.startsWith("java:jboss/ee/concurrency/executor/")) {
					return fixedThreadPool();
				}
				return null;
			}

			@Override
			public NameParser getNameParser(String name) {
				return CompositeName::new;
			}

			@Override
			public void close() {
			}
		});
	}

	@Bean("fixedThreadPool")
	ExecutorService fixedThreadPool() {
		return Executors.newFixedThreadPool(5);
	}

	@Bean
	@ConditionalOnMissingBean(name = "springBootPlatform")
	protected SimplePlatformProvider springBootPlatform() {
		return (SimplePlatformProvider) Platform.getPlatform();
	}

}
