package personal.wuyi.jibernate.exception;

public class DatabaseOperationException extends Exception {
	private static final long serialVersionUID = 1L;

	protected DatabaseOperationException() {
        super();
    }

    protected DatabaseOperationException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct a {@code DatabaseOperationException}
     * 
     * @param  message
     *         The error message in string.
     */
    public DatabaseOperationException(String message) {
        super(message);
    }

    /**
     * Construct a {@code DatabaseOperationException}
     * 
     * @param  message
     *         The error message in string.
     * 
     * @param  cause
     *         The exception.
     */
    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
