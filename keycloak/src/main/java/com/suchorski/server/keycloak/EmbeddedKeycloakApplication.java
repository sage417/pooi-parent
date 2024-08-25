package com.suchorski.server.keycloak;

import com.suchorski.server.keycloak.providers.RegularJsonConfigProviderFactory;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.Config;
import org.keycloak.exportimport.ExportImportManager;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.managers.ApplianceBootstrap;
import org.keycloak.services.resources.KeycloakApplication;
import org.keycloak.services.util.JsonConfigProviderFactory;

import java.util.NoSuchElementException;

@Slf4j
//@ApplicationPath("/")
public class EmbeddedKeycloakApplication extends KeycloakApplication {

	static KeycloakServerProperties properties;

	@Override
	protected void loadConfig() {
		JsonConfigProviderFactory factory = new RegularJsonConfigProviderFactory();
		Config.init(factory.create().orElseThrow(() -> new NoSuchElementException("No value present")));
	}

	@Override
	protected ExportImportManager bootstrap() {
		final ExportImportManager exportImportManager = super.bootstrap();
		createMasterRealmAdminUser();
		return exportImportManager;
	}

	private void createMasterRealmAdminUser() {
		try (KeycloakSession session = getSessionFactory().create()) {
			ApplianceBootstrap applianceBootstrap = new ApplianceBootstrap(session);
			try {
				session.getTransactionManager().begin();
				applianceBootstrap.createMasterRealmUser(properties.username(), properties.password());
				session.getTransactionManager().commit();
			} catch (Exception ex) {
				log.warn("Couldn't create keycloak master admin user: {}", ex.getMessage());
				session.getTransactionManager().rollback();
			}
		}
	}

}
