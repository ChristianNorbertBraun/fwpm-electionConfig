package de.fhws.fiw.fwom.electionConfig.database;

import de.fhws.fiw.fwpm.electionConfig.database.Persistency;
import de.fhws.fiw.fwpm.electionConfig.exceptionHandling.TableException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class PersistencyTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();


	@Test
	public void getInstanceTest() throws TableException {
		Persistency instance = Persistency.getInstance(false);

		assertNotNull(instance);
	}

	@Test
	public void getConnectionTest() throws TableException, SQLException {
		Persistency.reopenPool();
		Connection connection = Persistency.getInstance(false).getConnection();

		assertNotNull(connection);
	}

	@Test
	public void reopenPoolTest() throws TableException, SQLException {
		Persistency.getInstance(false).closeConnectionPool();
		Persistency.reopenPool();

		Connection connection = Persistency.getInstance(false).getConnection();

		assertNotNull(connection);

	}

	@Test(expected=SQLException.class)
	public void closeConnectionTest() throws TableException, SQLException {
		Persistency.getInstance(false).closeConnectionPool();

		Connection connection = Persistency.getInstance(false).getConnection();

		assertNull(connection);

		Persistency.reopenPool();
	}

}
