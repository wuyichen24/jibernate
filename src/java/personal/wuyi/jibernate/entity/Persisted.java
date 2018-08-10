package personal.wuyi.jibernate.entity;

import java.io.Serializable;

/**
 * The interface to define common operations for generic entity.
 * 
 * @author  Wuyi Chen
 * @date    08/08/2018
 * @version 1.0
 * @since   1.0
 */
public interface Persisted extends Serializable {
    /**
     * Get the {@code Uri} (Uniform Resource Identifier).
     * 
     * <p>This is commonly a string to represent the uniqueness of entity.
     *
     * @return  The {@code Uri}
     */
    Uri getUri();

    /**
     * Check if an entity has been persisted or not.
     * 
     * <p>If an entity in Java code has a corresponding record in database, 
     * it is persisted.
     * 
     * <p>There are 2 common cases to explain:
     * <ul>
     *   <li>If you created an entity in code but didn't insert it into 
     *   database, that entity is not persisted. After inserting it, that 
     *   entity is persisted.
     *   <li>If you retrieved an entity from database, even if you modified 
     *   that entity, that entity is still persisted.
     * </ul>
     * 
     * @return
     */
    boolean isPersisted();
}
