package de.fhws.fiw.fwpm.electionConfig.database.dao;

import com.google.common.base.Optional;
import de.fhws.fiw.fwpm.electionConfig.database.table.ElectionPeriodTable;
import de.fhws.fiw.fwpm.electionConfig.exceptionHandling.DAOException;
import de.fhws.fiw.fwpm.electionConfig.models.ElectionPeriod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class ElectionPeriodDAO extends AbstractDAO implements ElectionPeriod.Fields, ElectionPeriodTable.Fields {

	private final ZoneId timeZone = ZoneId.of("Europe/Berlin");

	private PreparedStatement preparedStatement;

	public ElectionPeriodDAO(Connection connection) {
		super(connection);
	}

	public Optional<ElectionPeriod> create(ElectionPeriod electionPeriod) throws DAOException {
		Optional<ElectionPeriod> returnValue = Optional.fromNullable(null);
		try {
			String sqlStatement = "INSERT INTO " + TABLE_NAME + " (" + START_DATE + ", " + END_DATE + ", " + TITLE + ", " + TRIGGERED
					+ ") VALUES (?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setTimestamp(1, electionPeriod.getSqlStartDate());
			preparedStatement.setTimestamp(2, electionPeriod.getSqlEndDate());
			preparedStatement.setString(3, electionPeriod.getTitle());
			preparedStatement.setBoolean(4, electionPeriod.isElectionTriggered());
			int effectedRows = preparedStatement.executeUpdate();
			electionPeriod.setPeriodId(getGeneratedId(preparedStatement));
			if (effectedRows > 0) {
				returnValue = Optional.fromNullable(electionPeriod);
			}

		} catch (SQLException ex) {
			if (ex.getSQLState().startsWith("23")) {
				throw new DateTimeException("Duplicate date in database");
			}
			ex.printStackTrace();
			throw new DAOException("SQL-Problem", ex);
		} finally {
			closeConnection();
		}
		return returnValue;
	}

	public Optional<ElectionPeriod> update(ElectionPeriod electionPeriod) throws DAOException {
		Optional<ElectionPeriod> returnValue = Optional.fromNullable(null);
		try {
			preparedStatement = connection.prepareStatement("UPDATE " + TABLE_NAME + " SET " +
							START_DATE + " = ?, " +
							END_DATE + " = ?, " +
					TITLE + " = ?, " +
							TRIGGERED + " = ? " +
							"WHERE " + PERIOD_ID + " = ?");
			preparedStatement.setTimestamp(1, electionPeriod.getSqlStartDate());
			preparedStatement.setTimestamp(2, electionPeriod.getSqlEndDate());
			preparedStatement.setString(3, electionPeriod.getTitle());
			preparedStatement.setBoolean(4, electionPeriod.isElectionTriggered());
			preparedStatement.setLong(5, electionPeriod.getPeriodId());
			int effectedRows = preparedStatement.executeUpdate();

			if (effectedRows > 0) {
				returnValue = Optional.fromNullable(electionPeriod);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new DAOException("SQL-Problem", ex);
		} finally {
			closeConnection();
		}

		return returnValue;
	}

	public void delete(long periodId) throws DAOException {
		try {
			preparedStatement = connection.prepareStatement("DELETE FROM " + TABLE_NAME + " WHERE " + PERIOD_ID + "= ?");
			preparedStatement.setLong(1, periodId);
			int effectedRows = deleteFromDB(preparedStatement);

			if (effectedRows <= 0) {
				throw new DAOException("nothing deleted");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public Optional<ElectionPeriod> getLatest() throws DAOException {
		Optional<ElectionPeriod> returnValue = Optional.fromNullable(null);
		try {
			preparedStatement = connection.prepareStatement(
					"SELECT * FROM " + TABLE_NAME + " WHERE " + END_DATE + " IN (SELECT max(" + END_DATE + " ) FROM " + TABLE_NAME + " )");
			List<ElectionPeriod> result = resultToList(readFromDB(preparedStatement));
			if (result.size() > 0) {
				returnValue = Optional.fromNullable(result.get(0));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new DAOException(ex);
		}

		return returnValue;

	}

	public Optional<ElectionPeriod> findByPeriod(long periodId) throws DAOException {
		Optional<ElectionPeriod> returnValue = Optional.fromNullable(null);
		try {
			preparedStatement = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE " + PERIOD_ID + " = ?");
			preparedStatement.setLong(1, periodId);
			List<ElectionPeriod> result = resultToList(readFromDB(preparedStatement));
			if (result.size() > 0) {
				returnValue = Optional.fromNullable(result.get(0));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new DAOException(ex);
		}

		return returnValue;
	}

	public List<ElectionPeriod> listAll() throws DAOException {
		List<ElectionPeriod> returnValue = new ArrayList<>();
		try {
			preparedStatement = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " ORDER BY " + END_DATE + " DESC");
			returnValue.addAll(resultToList(readFromDB(preparedStatement)));
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new DAOException(ex);
		}
		return returnValue;
	}

	private List<ElectionPeriod> resultToList(ResultSet resultSet) throws DAOException {
		List<ElectionPeriod> electionPeriods = new ArrayList<>();

		try {
			while (resultSet.next()) {
				ElectionPeriod current = new ElectionPeriod();
				current.setPeriodId(resultSet.getLong(PERIOD_ID));
				current.setStartDate(ZonedDateTime.ofInstant(resultSet.getTimestamp(START_DATE).toInstant(), timeZone));
				current.setEndDate(ZonedDateTime.ofInstant(resultSet.getTimestamp(END_DATE).toInstant(), timeZone));
				current.setTitle(resultSet.getString(TITLE));
				current.setIsElectionTriggered(resultSet.getBoolean(TRIGGERED));
				electionPeriods.add(current);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new DAOException("Couldn't parse resultset to list", ex);
		} finally {
			closeResultSet(resultSet);
			closeConnection();
		}
		return electionPeriods;
	}
}
