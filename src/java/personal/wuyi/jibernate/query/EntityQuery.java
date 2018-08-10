package personal.wuyi.jibernate.query;

import personal.wuyi.jibernate.entity.Persisted;

/**
 * EntityQuery
 *
 * <p>Query criteria, sort, etc. will be resolved to JPQL. If explicit JPQL is 
 * specific, then that will override any other query attributes.
 * 
 * @author Wuyi Chen
 */
public class EntityQuery<E extends Persisted> extends Query<E> {
    protected String jpql;

    protected EntityQuery() {}

    /**
     * Construct a {@code EntityQuery}
     * @param persistedClass
     */
    public EntityQuery(Class<E> persistedClass) {
        super(persistedClass);
    }

    public String getJpql() {
        return jpql;
    }

    public void setJpql(String jpql) {
        this.jpql = jpql;
    }
}
