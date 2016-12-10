package de.fhws.fiw.fwpm.electionConfig.database.table;

import de.fhws.fiw.fwpm.electionConfig.exceptionHandling.TableException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class AbstractTable {

	protected abstract String getTableName();

	public final void prepareTable(boolean delete, Connection connection) throws TableException, SQLException {
		if (delete) {
			deleteTable(connection);
		}

		createTable(connection);
	}

	protected abstract void createTable(Connection connection) throws TableException;

	protected final void deleteTable(Connection connection) throws TableException, SQLException {
		final StringBuffer sb = new StringBuffer();

		sb.append("DROP TABLE IF EXISTS ");
		sb.append(getTableName());
		sb.append(";");

		final Statement statement = connection.createStatement();
		statement.executeUpdate(sb.toString());
	}
}
