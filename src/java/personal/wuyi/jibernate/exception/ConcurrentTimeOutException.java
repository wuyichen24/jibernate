package personal.wuyi.jibernate.exception;

public class ConcurrentTimeOutException extends Exception {
	private static final long serialVersionUID = 1L;

	protected ConcurrentTimeOutException() {
        super();
    }

    protected ConcurrentTimeOutException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct a {@code EmailException}
     * 
     * @param  message
     *         The error message in string.
     */
    public ConcurrentTimeOutException(String message) {
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
    public ConcurrentTimeOutException(String message, Throwable cause) {
        super(message, cause);
    }
}
