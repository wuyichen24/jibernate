package personal.wuyi.jibernate.query;

import personal.wuyi.jibernate.entity.Persisted;

/**
 * The class represent the query to the database.
 *
 * @author  Wuyi Chen
 * @date    09/19/2018
 * @version 1.0
 * @since   1.0
 */
public class EntityQuery<E extends Persisted> extends JQuery<E> {
    protected String jpql;

    /**
     * Constructs a {@code EntityQuery}.
     * 
     * @since   1.0
     */
    protected EntityQuery() {}

    /**
     * Constructs a {@code EntityQuery}.
     * 
     * @param  clazz
     *         The class of an entity needs to be queried.
     * 
     * @since   1.0
     */
    public EntityQuery(Class<E> clazz) {
        super(clazz);
    }

    public String getJpql()            { return jpql;      }
    public void   setJpql(String jpql) { this.jpql = jpql; }
}
