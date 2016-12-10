package de.fhws.fiw.fwpm.electionConfig.exceptionHandling;

public class DAOException extends Exception {

	public DAOException() {
	}

	public DAOException(String message) {
		super(message);
	}

	public DAOException(String message, Throwable cause) {
		super(message, cause);
	}

	public DAOException(Throwable cause) {
		super(cause);
	}
}
