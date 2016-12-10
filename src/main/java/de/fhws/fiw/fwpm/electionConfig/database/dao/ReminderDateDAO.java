package de.fhws.fiw.fwpm.electionConfig.database.dao;

import com.google.common.base.Optional;
import de.fhws.fiw.fwpm.electionConfig.database.table.ReminderDateTable;
import de.fhws.fiw.fwpm.electionConfig.exceptionHandling.DAOException;
import de.fhws.fiw.fwpm.electionConfig.models.ReminderDate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReminderDateDAO extends AbstractDAO implements ReminderDate.Fields, ReminderDateTable.Fields {

	private final ZoneId timeZone = ZoneId.of("Europe/Berlin");

	private PreparedStatement preparedStatement;

	public ReminderDateDAO(Connection connection) {
		super(connection);
	}

	public Optional<ReminderDate> create(ReminderDate reminderDate) throws DAOException {
		Optional<ReminderDate> returnValue = Optional.fromNullable(null);
		try {
			preparedStatement = connection.prepareStatement(
					"INSERT INTO " + TABLE_NAME + " (" + PERIOD_ID + ", " + REMINDER_DATE + ", " + MAIL_SENT + ") VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setLong(1, reminderDate.getPeriodId());
			preparedStatement.setTimestamp(2, reminderDate.getSqlDate());
			preparedStatement.setBoolean(3, reminderDate.isMailSent());
			int effectedRows = preparedStatement.executeUpdate();

			if (effectedRows > 0) {
				returnValue = Optional.fromNullable(reminderDate);
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new DAOException("SQL-Problem", ex);
		} finally {
			closeConnection();
		}
		return returnValue;
	}

	public Optional<ReminderDate> update(ReminderDate reminderDate) throws DAOException {
		Optional<ReminderDate> returnValue = Optional.fromNullable(null);
		try {
			preparedStatement = connection.prepareStatement(
					"UPDATE " + TABLE_NAME + " SET " +
							MAIL_SENT + " = ? "
							+ "WHERE " + PERIOD_ID + " = ? AND " +
							REMINDER_DATE + " = ? ");
			preparedStatement.setBoolean(1, reminderDate.isMailSent());
			preparedStatement.setLong(2, reminderDate.getPeriodId());
			preparedStatement.setTimestamp(3, reminderDate.getSqlDate());
			int effectedRows = preparedStatement.executeUpdate();

			if (effectedRows > 0) {
				returnValue = Optional.fromNullable(reminderDate);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new DAOException("SQL-Problem", ex);
		} finally {
			closeConnection();
		}

		return returnValue;
	}

	public void deleteByPeriodId(long periodID) throws DAOException {
		try {
			preparedStatement = connection.prepareStatement(
					"DELETE FROM " + TABLE_NAME + " WHERE " + PERIOD_ID + "= ?");
			preparedStatement.setLong(1, periodID);
			preparedStatement.execute();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public List<ReminderDate> findByPeriod(long periodId) throws DAOException {

		try {
			preparedStatement = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE " + PERIOD_ID + " = ?");
			preparedStatement.setLong(1, periodId);

		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new DAOException("SQL-Problem", ex);
		}

		return resultToList(readFromDB(preparedStatement));
	}

	private List<ReminderDate> resultToList(ResultSet resultSet) throws DAOException {
		List<ReminderDate> reminderDates = new ArrayList<>();
		try {
			while (resultSet.next()) {
				ReminderDate current = new ReminderDate();
				current.setPeriodId(resultSet.getLong(PERIOD_ID));
				current.setDate(ZonedDateTime.ofInstant(resultSet.getTimestamp(REMINDER_DATE).toInstant(), timeZone));
				current.setMailSent(resultSet.getBoolean(MAIL_SENT));

				reminderDates.add(current);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new DAOException("Couldn't parse resultset to list", ex);
		} finally {
			closeResultSet(resultSet);
			closeConnection();
		}

		return reminderDates;
	}
}

