package org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.jaas;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Paths;

import javax.security.auth.login.Configuration;

import org.junit.jupiter.api.Test;

class JaasConfigurationTest {
	@Test
	void shouldLoadBothJaasDomainsFromConfigFile() {
		String jaasConfigPath = Paths.get("jaas.conf").toAbsolutePath().toString();
		System.setProperty("java.security.auth.login.config", jaasConfigPath);
		Configuration.getConfiguration().refresh();

		assertNotNull(Configuration.getConfiguration().getAppConfigurationEntry("MasterAnnonceLogin"));
		assertNotNull(Configuration.getConfiguration().getAppConfigurationEntry("MasterAnnonceToken"));
	}
}
