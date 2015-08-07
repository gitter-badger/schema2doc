package org.manathome.schema2doc.scanner;

/** scanner exceptions. */
public class ScannerException extends RuntimeException {

	private static final long serialVersionUID = -304710658929159406L;

	public ScannerException() {
	}

	public ScannerException(String msg) {
		super(msg);
	}

	public ScannerException(String msg, Throwable exc) {
		super(msg, exc);
	}
}
