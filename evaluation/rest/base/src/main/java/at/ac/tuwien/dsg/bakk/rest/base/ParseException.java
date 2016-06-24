package at.ac.tuwien.dsg.bakk.rest.base;

/**
 * Exception indicating that the bean parsing failed.
 */
public class ParseException extends RuntimeException {
	private static final long serialVersionUID = 1992924693154134158L;

	public ParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
