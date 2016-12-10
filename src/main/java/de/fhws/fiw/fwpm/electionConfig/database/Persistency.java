package de.fhws.fiw.fwpm.electionConfig.database;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.fhws.fiw.fwpm.electionConfig.PropertySingleton;
import de.fhws.fiw.fwpm.electionConfig.database.table.AbstractTable;
import de.fhws.fiw.fwpm.electionConfig.database.table.ElectionPeriodTable;
import de.fhws.fiw.fwpm.electionConfig.database.table.ParticipatedFwpmTable;
import de.fhws.fiw.fwpm.electionConfig.database.table.ReminderDateTable;
import de.fhws.fiw.fwpm.electionConfig.exceptionHandling.TableException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class Persistency {

	private static final String COM_MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DATABASE_PORT = "3306";
	private static String databaseHost;
	private static String databaseName;
	private static Persistency instance;

	private static int counter = 0;
	private String username;
	private String password;

	private ComboPooledDataSource cpds;

	//only for testing
	public static void reopenPool() throws TableException {
		getInstance(false).createConnectionPool();
	}

	public static Persistency getInstance(boolean deleteDatabase) throws TableException {
		if (instance == null) {
			instance = new Persistency(deleteDatabase);
		}
		return instance;
	}

	private Persistency(boolean deleteDatabase) throws TableException {
		readProperties();
		createConnectionPool();
		createAllTables(deleteDatabase);
	}

	public Connection getConnection() throws SQLException {
		return cpds.getConnection();
	}

	public void closeConnectionPool() {
		cpds.close();
	}

	protected void createConnectionPool() {
		try {
			Class.forName(COM_MYSQL_JDBC_DRIVER);
			cpds = new ComboPooledDataSource();
			cpds.setDriverClass(COM_MYSQL_JDBC_DRIVER);
			cpds.setJdbcUrl(
					"jdbc:mysql://" + databaseHost + ":" + DATABASE_PORT + "/" + databaseName + "?autoReconnect=true&useSSL=false");
			cpds.setUser(username);
			cpds.setPassword(password);
			cpds.setTestConnectionOnCheckout(true);
			cpds.setMinPoolSize(5);
			cpds.setAcquireIncrement(5);
		} catch (Exception ex) {
			// Mysql driver not found
			cpds = null;
		}
	}

	protected void createAllTables(boolean deleteDatabase) throws TableException {
		Connection connection = null;
		final List<AbstractTable> tables = getAllTables();

		try {
			connection = getConnection();
			for (AbstractTable table : tables) {
				table.prepareTable(deleteDatabase, connection);
			}
		} catch (TableException ex) {
			if (counter < 3) {
				counter++;
				createAllTables(deleteDatabase);
			} else {
				throw new TableException(ex);
			}
		} catch (SQLException ex) {
			throw new TableException("SQL-Probmelm", ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private List<AbstractTable> getAllTables() {
		List<AbstractTable> tables = new ArrayList<>();
		tables.add(new ElectionPeriodTable());
		tables.add(new ParticipatedFwpmTable());
		tables.add(new ReminderDateTable());

		return tables;
	}

	private void readProperties() throws TableException {

		try {
			Properties properties = PropertySingleton.getInstance();

			databaseHost = properties.getProperty("DATABASE_HOST");
			databaseName = properties.getProperty("DATABASE_NAME");
			username = properties.getProperty("DATABASE_USER");
			password = properties.getProperty("DATABASE_PASSWORD");

		} catch (IOException ex) {
			throw new TableException("could't not find property file");
		}
	}
}