package de.fhws.fiw.fwpm.electionConfig.database.table;

import de.fhws.fiw.fwpm.electionConfig.exceptionHandling.TableException;
import de.fhws.fiw.fwpm.electionConfig.models.ParticipatedFwpm;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ParticipatedFwpmTable extends AbstractTable implements ParticipatedFwpm.Fields {

	public interface Fields {
		String TABLE_NAME = "ParticipatedFWPM";
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
			sb.append(PERIOD_ID + " bigint unsigned NOT NULL,");
			sb.append(FWPM_ID + " varchar(255) NOT NULL,");
			sb.append("PRIMARY KEY (" + PERIOD_ID + ", " + FWPM_ID + "),");
			sb.append("FOREIGN KEY (" + PERIOD_ID + ") REFERENCES " + ElectionPeriodTable.Fields.TABLE_NAME + "(" + PERIOD_ID + ")");
			sb.append(" ON DELETE CASCADE");
			sb.append(")");

			final Statement statement = connection.createStatement();
			statement.executeUpdate(sb.toString());
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new TableException("couldn't create table", ex);
		}
	}
}
