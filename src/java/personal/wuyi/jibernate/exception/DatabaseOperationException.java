package personal.wuyi.jibernate.exception;

/**
 * The exception for database operations.
 * 
 * @author  Wuyi Chen
 * @date    08/08/2018
 * @version 1.0
 * @since   1.0
 */
public class DatabaseOperationException extends Exception {
	private static final long serialVersionUID = 1L;

    /**
     * Construct a {@code DatabaseOperationException}.
     * 
     * @since   1.0
     */
	protected DatabaseOperationException() {
        super();
    }

    /**
     * Construct a {@code DatabaseOperationException}.
     * 
     * @param  cause
     *         The exception.
     *         
     * @since   1.0
     */
    protected DatabaseOperationException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct a {@code DatabaseOperationException}.
     * 
     * @param  message
     *         The error message in string.
     *         
     * @since   1.0
     */
    public DatabaseOperationException(String message) {
        super(message);
    }

    /**
     * Construct a {@code DatabaseOperationException}.
     * 
     * @param  message
     *         The error message in string.
     * 
     * @param  cause
     *         The exception.
     *         
     * @since   1.0
     */
    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
