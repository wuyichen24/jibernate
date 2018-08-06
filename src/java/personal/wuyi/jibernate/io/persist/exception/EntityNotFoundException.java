package personal.wuyi.autostock.io.persist.exception;

public class EntityNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	protected EntityNotFoundException() {
        super();
    }

    protected EntityNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct a {@code EmailException}
     * 
     * @param  message
     *         The error message in string.
     */
    public EntityNotFoundException(String message) {
        super(message);
    }

    /**
     * Construct a {@code EmailException}
     * 
     * @param  message
     *         The error message in string.
     * 
     * @param  cause
     *         The exception.
     */
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
