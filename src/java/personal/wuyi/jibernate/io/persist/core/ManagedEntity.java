package personal.wuyi.jibernate.io.persist.core;

/**
 * ManagedEntity
 */
public interface ManagedEntity extends Persisted {
    /**
     * Return the primary key
     *
     * @return
     */
    Object getId();

    @Override
    default Uri getUri() {
        return new Uri(this.getClass(), getId());
    }
}
