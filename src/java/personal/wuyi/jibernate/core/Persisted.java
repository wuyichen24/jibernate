package personal.wuyi.jibernate.core;

import java.io.Serializable;

public interface Persisted extends Serializable {
    /**
     * Get the URI
     *
     * @return
     */
    Uri getUri();

    /**
     * Check if a object has been persisted
     * 
     * @return
     */
    boolean isPersisted();
}
