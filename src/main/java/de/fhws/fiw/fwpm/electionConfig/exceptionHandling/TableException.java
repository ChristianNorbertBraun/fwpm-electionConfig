package de.fhws.fiw.fwpm.electionConfig.exceptionHandling;

public class TableException extends Exception {

	public TableException() {
	}

	public TableException(String message) {
		super(message);
	}

	public TableException(String message, Throwable cause) {
		super(message, cause);
	}

	public TableException(Throwable cause) {
		super(cause);
	}
}
