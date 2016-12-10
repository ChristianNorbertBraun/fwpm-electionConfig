package de.fhws.fiw.fwpm.electionConfig.database.table;

import de.fhws.fiw.fwpm.electionConfig.exceptionHandling.TableException;
import de.fhws.fiw.fwpm.electionConfig.models.ElectionPeriod;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ElectionPeriodTable extends AbstractTable implements ElectionPeriod.Fields {

	public interface Fields {
		String TABLE_NAME = "ElectionPeriod";
	}

	@Override
	protected String getTableName() {
		return Fields.TABLE_NAME;
	}

	@Override
	protected void createTable(Connection connection) throws TableException {
		try {
			final StringBuffer sb = new StringBuffer();

			sb.append("CREATE TABLE IF NOT EXISTS " + Fields.TABLE_NAME + "(");
			sb.append(PERIOD_ID + " bigint unsigned NOT NULL AUTO_INCREMENT,");
			sb.append(START_DATE + " DATETIME UNIQUE NOT NULL,");
			sb.append(END_DATE + " DATETIME UNIQUE NOT NULL,");
			sb.append(TITLE + " varchar(255) NOT NULL,");
			sb.append(TRIGGERED + " BOOLEAN,");
			sb.append("PRIMARY KEY (" + PERIOD_ID + ")");
			sb.append(")");

			final Statement statement = connection.createStatement();
			statement.executeUpdate(sb.toString());
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new TableException("couldn't create table", ex);
		}
	}
}
