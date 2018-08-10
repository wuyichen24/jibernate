package personal.wuyi.jibernate.entity;


/**
 * The interface to define common managed entity.
 * 
 * @author  Wuyi Chen
 * @date    08/08/2018
 * @version 1.0
 * @since   1.0
 */
public interface ManagedEntity extends Persisted {
    /**
     * Return the primary key.
     * 
     * <p>This is the unique identifier among entities in same type.
     *
     * @return  The object representing the primary key.
     * 
     * @since   1.0
     */
    Object getId();

    @Override
    default Uri getUri() {
        return new Uri(this.getClass(), getId());
    }
}
