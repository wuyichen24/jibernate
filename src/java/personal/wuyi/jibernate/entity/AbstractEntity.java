package personal.wuyi.jibernate.entity;

/**
 * The abstract class for implementing generic entity classes.
 * 
 * <p>All other entity classes on your own should inherit this class.
 * 
 * @author  Wuyi Chen
 * @date    08/08/2018
 * @version 1.0
 * @since   1.0
 */
abstract class AbstractEntity implements ManagedEntity {
	private static final long serialVersionUID = 1L;

    @Override
	abstract public Long getId();

    @Override
    public Uri getUri() {
        return new Uri(this.getClass(), this.getId());
    }

    @Override
    public boolean isPersisted() {
        return (getId() != null);
    }
}
