/*
 * Copyright 2018 Wuyi Chen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package personal.wuyi.jibernate.exception;

/**
 * The exception for database operations.
 * 
 * @author  Wuyi Chen
 * @date    08/08/2018
 * @version 1.1
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
