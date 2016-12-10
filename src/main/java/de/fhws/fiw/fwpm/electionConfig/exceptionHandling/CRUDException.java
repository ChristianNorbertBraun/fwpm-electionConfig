package de.fhws.fiw.fwpm.electionConfig.exceptionHandling;

public class CRUDException extends Exception {
	public CRUDException() {
	}

	public CRUDException(String message) {
		super(message);
	}

	public CRUDException(String message, Throwable cause) {
		super(message, cause);
	}

	public CRUDException(Throwable cause) {
		super(cause);
	}
}
