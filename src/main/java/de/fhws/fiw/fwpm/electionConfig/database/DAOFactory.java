package de.fhws.fiw.fwpm.electionConfig.database;

import de.fhws.fiw.fwpm.electionConfig.database.dao.ElectionPeriodDAO;
import de.fhws.fiw.fwpm.electionConfig.database.dao.ParticipatedFwpmDAO;
import de.fhws.fiw.fwpm.electionConfig.database.dao.ReminderDateDAO;
import de.fhws.fiw.fwpm.electionConfig.exceptionHandling.TableException;

import java.sql.SQLException;

public final class DAOFactory {

	private static DAOFactory factory = null;
	private Persistency persistency;

	public static DAOFactory getInstance() throws TableException {
		return getInstance(false);
	}

	public static DAOFactory getInstance(boolean deleteDatabase) throws TableException {
		if (factory == null) {
			factory = new DAOFactory(deleteDatabase);
		}
		return factory;
	}

	private DAOFactory(boolean deleteDatabase) throws TableException {
		persistency = Persistency.getInstance(deleteDatabase);
	}

	public ElectionPeriodDAO createElectionPeriodDAO() throws SQLException {

		return new ElectionPeriodDAO(persistency.getConnection());
	}

	public ParticipatedFwpmDAO createParticipatedFwpmDAO() throws SQLException {

		return new ParticipatedFwpmDAO(persistency.getConnection());
	}

	public ReminderDateDAO createReminderDateDAO() throws SQLException {

		return new ReminderDateDAO(persistency.getConnection());
	}

}
