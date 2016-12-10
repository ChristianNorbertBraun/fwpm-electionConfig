package de.fhws.fiw.fwpm.electionConfig.database.dao;

import de.fhws.fiw.fwpm.electionConfig.exceptionHandling.DAOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractDAO {

	protected Connection connection;

	public AbstractDAO(Connection connection) {
		this.connection = connection;
	}

	protected void closeConnection() throws DAOException {
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new DAOException("Couldn't close connection", ex);
			}
		}
	}

	protected void closeResultSet(ResultSet resultSet) throws DAOException {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				throw new DAOException("Couldn't close resultset", ex);
			}
		}
	}

	protected long getGeneratedId(PreparedStatement preparedStatement) throws SQLException, DAOException {
		long id = -1;
		ResultSet rs = preparedStatement.getGeneratedKeys();

		if (rs.next()) {
			id = rs.getLong(1);
		}

		closeResultSet(rs);

		return id;
	}

	protected ResultSet readFromDB(PreparedStatement preparedStatement) {
		ResultSet resultSet = null;

		try {
			resultSet = preparedStatement.executeQuery();

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return resultSet;
	}

	public int deleteFromDB(PreparedStatement preparedStatement) throws SQLException, DAOException {
		int effectedRows = 0;
		try {
			effectedRows = preparedStatement.executeUpdate();

		} finally {
			closeConnection();
		}
		return effectedRows;
	}
}
