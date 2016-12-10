package de.fhws.fiw.fwom.electionConfig;

import de.fhws.fiw.fwpm.electionConfig.PropertySingleton;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.junit.Assert.*;
public class PropertySingletonTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void readElectionConfigPropertiesTest() throws IOException {
		PropertySingleton.getInstance().getProperty("");
	}

	@Test
	public void readAllDBPropertiesTest() throws IOException {
		String dbHost = PropertySingleton.getInstance().getProperty("DATABASE_HOST");
		String dbName = PropertySingleton.getInstance().getProperty("DATABASE_NAME");
		String dbUser = PropertySingleton.getInstance().getProperty("DATABASE_USER");
		String dbPassword = PropertySingleton.getInstance().getProperty("DATABASE_PASSWORD");

		assertFalse(dbHost.isEmpty());
		assertFalse(dbName.isEmpty());
		assertFalse(dbUser.isEmpty());
		assertFalse(dbPassword.isEmpty());
	}

	@Test
	public void readAllNeededUrlsTest() throws IOException {
		String authUrl = PropertySingleton.getInstance().getProperty("AUTH_URL");
		String assignmentUrl = PropertySingleton.getInstance().getProperty("ASSIGNMENTS_URL");

		assertFalse(authUrl.isEmpty());
		assertFalse(assignmentUrl.isEmpty());
	}

	@Test
	public void readEnvironmentTest() throws IOException {
		String env = PropertySingleton.getInstance().getProperty("ENVIRONMENT");

		assertFalse(env.isEmpty());
	}

	@Test
	public void readAllNeededAPIKeysTest() throws IOException {
		String self = PropertySingleton.getInstance().getProperty("SELF");
		String assignmentKey = PropertySingleton.getInstance().getProperty("ASSIGNMENT");

		assertFalse(self.isEmpty());
		assertFalse(assignmentKey.isEmpty());
	}
}
