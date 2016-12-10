package de.fhws.fiw.fwpm.electionConfig.database.dao;

import de.fhws.fiw.fwpm.electionConfig.database.table.ParticipatedFwpmTable;
import de.fhws.fiw.fwpm.electionConfig.exceptionHandling.DAOException;
import de.fhws.fiw.fwpm.electionConfig.models.ParticipatedFwpm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.google.common.base.Optional;

public class ParticipatedFwpmDAO extends AbstractDAO implements ParticipatedFwpmTable.Fields, ParticipatedFwpm.Fields {

	private PreparedStatement preparedStatement;

	public ParticipatedFwpmDAO(Connection connection) {
		super(connection);
	}

	public Optional<ParticipatedFwpm> create(ParticipatedFwpm participatedFwpm) throws DAOException {
		Optional<ParticipatedFwpm> returnValue = Optional.fromNullable(null);
		try {
			preparedStatement = connection.prepareStatement(
					"INSERT INTO " + TABLE_NAME + " (" + PERIOD_ID + ", " + FWPM_ID + ") VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setLong(1, participatedFwpm.getPeriodId());
			preparedStatement.setString(2, participatedFwpm.getFwpmId());
			int effectedRows = preparedStatement.executeUpdate();

			if (effectedRows > 0) {
				returnValue = Optional.fromNullable(participatedFwpm);
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new DAOException("SQL-Problem", ex);

		} finally {
			closeConnection();
		}
		return returnValue;
	}

	public void deleteByPeriodId(long periodId) throws DAOException {

		try {
			preparedStatement = connection.prepareStatement(
					"DELETE FROM " + TABLE_NAME + " WHERE " + PERIOD_ID + "= ?");
			preparedStatement.setLong(1, periodId);
			preparedStatement.execute();
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new DAOException(ex);
		}

	}

	public List<ParticipatedFwpm> findByPeriod(long periodId) throws DAOException {

		try {
			preparedStatement = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE " + PERIOD_ID + " = ?");
			preparedStatement.setLong(1, periodId);

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return resultToList(readFromDB(preparedStatement));
	}

	private List<ParticipatedFwpm> resultToList(ResultSet resultSet) throws DAOException {
		List<ParticipatedFwpm> participatedFwpms = new ArrayList<>();

		try {
			while (resultSet.next()) {
				ParticipatedFwpm current = new ParticipatedFwpm();
				current.setPeriodId(resultSet.getLong(1));
				current.setFwpmId(resultSet.getString(2));

				participatedFwpms.add(current);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new DAOException("SQL-Problem", ex);
		} finally {
			closeResultSet(resultSet);
			closeConnection();
		}

		return participatedFwpms;
	}
}
