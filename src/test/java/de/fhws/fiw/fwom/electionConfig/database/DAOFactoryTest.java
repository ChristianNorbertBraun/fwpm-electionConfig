package de.fhws.fiw.fwom.electionConfig.database;

import de.fhws.fiw.fwpm.electionConfig.database.DAOFactory;
import de.fhws.fiw.fwpm.electionConfig.database.Persistency;
import de.fhws.fiw.fwpm.electionConfig.database.dao.ElectionPeriodDAO;
import de.fhws.fiw.fwpm.electionConfig.database.dao.ParticipatedFwpmDAO;
import de.fhws.fiw.fwpm.electionConfig.database.dao.ReminderDateDAO;
import de.fhws.fiw.fwpm.electionConfig.exceptionHandling.TableException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class DAOFactoryTest {

	@Before
	public void init() {
		try {
			Persistency.reopenPool();
		} catch (Exception ex) {

		}
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void getInstanceTest() throws TableException {
		DAOFactory daoFactory = DAOFactory.getInstance();

		assertNotNull(daoFactory);
	}

	@Test
	public void createElectionPeriodDAOTest() throws TableException, SQLException {
		ElectionPeriodDAO electionPeriodDAO = DAOFactory.getInstance().createElectionPeriodDAO();

		assertNotNull(electionPeriodDAO);
	}

	@Test
	public void createParticipatedFwpmDAOTest() throws TableException, SQLException {
		ParticipatedFwpmDAO participatedFwpmDAO = DAOFactory.getInstance().createParticipatedFwpmDAO();

		assertNotNull(participatedFwpmDAO);
	}

	@Test
	public void createReminderDateDAOTest() throws TableException, SQLException {
		ReminderDateDAO reminderDateDAO = DAOFactory.getInstance().createReminderDateDAO();

		assertNotNull(reminderDateDAO);
	}

	@Test(expected=SQLException.class)
	public void createElectionPeriodDAOClosedConnectionTest() throws TableException, SQLException {
		closeConnection();
		ElectionPeriodDAO electionPeriodDAO = DAOFactory.getInstance().createElectionPeriodDAO();

		assertNotNull(electionPeriodDAO);

		reopenConnection();
	}

	@Test(expected=SQLException.class)
	public void createParticipatedFwpmDAOClosedConnectionTest() throws TableException, SQLException {
		closeConnection();
		ParticipatedFwpmDAO participatedFwpmDAO = DAOFactory.getInstance().createParticipatedFwpmDAO();

		assertNotNull(participatedFwpmDAO);

		reopenConnection();
	}

	@Test(expected=SQLException.class)
	public void createReminderDateDAOClosedConnectionTest() throws TableException, SQLException {
		closeConnection();
		ReminderDateDAO reminderDateDAO = DAOFactory.getInstance().createReminderDateDAO();

		assertNotNull(reminderDateDAO);

		reopenConnection();
	}

	private void closeConnection() throws TableException {
		Persistency.getInstance(false).closeConnectionPool();
	}

	private void reopenConnection() throws TableException {
		Persistency.reopenPool();
	}

}
