package personal.wuyi.jibernate.entitymanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import personal.wuyi.jibernate.entity.ManagedEntity;
import personal.wuyi.jibernate.entity.Persisted;
import personal.wuyi.jibernate.entity.Uri;
import personal.wuyi.jibernate.exception.DatabaseOperationException;
import personal.wuyi.jibernate.query.JQuery;
import personal.wuyi.jibernate.query.QueryConverter;
import personal.wuyi.reflect.ReflectUtil;

/**
 * The generic DAO (Data Access Object) for processing database operations.
 * 
 * <p>Implements DAO functionality based on JPA EntityManager. Concrete 
 * EntityManager DAO implementations should be specific to a persistence unit.
 * 
 * <p>This class is an abstract class so that it is generic and other 
 * database-specific DAO class (like MysqlDao or OracleDao) needs to inherit 
 * this class.
 * 
 * @author  Wuyi Chen
 * @date    08/07/2018
 * @version 1.0
 * @since   1.0
 */
abstract class AbstractEntityManagerDao implements Dao {
	private EntityManagerFactory entityManagerFactory;
	
	private static Logger logger = LoggerFactory.getLogger(AbstractEntityManagerDao.class);
	
	/**
	 * Get the Hibernate dialect string corresponding to DB technology (MySQL, Oracle, etc.)
	 * 
	 * @return  The string of dialect.
	 * 
     * @since   1.0
	 */
	abstract String     getDialect();
	
	/**
     * Build the {@code DataSource} used by current {@code EntityManager} 
     * which is specific to DB technology (MySQL, Oracle, etc.),
     * host/environment and credentials.
     *
     * @return  The {@code DataSource}.
     * 
     * @since   1.0
     */
	abstract DataSource getDataSource();
	
	/**
	 * Get the name of persistence unit.
	 * 
	 * @return  The name of persistence unit.
	 * 
     * @since   1.0
	 */
	abstract String     getPersistenceUnit();
	
	/* (non-Javadoc)
	 * @see personal.wuyi.jibernate.core.Dao#read(personal.wuyi.jibernate.core.Uri)
	 */
	@Override
	@SuppressWarnings("unchecked")
    public <T extends Persisted> T read(Uri uri) {
        final Object id = uri.getId();
        final EntityManager entityManager = getEntityManager();

        try {
            return (T) entityManager.find(uri.getType(), id);
        } finally {
            entityManager.close();
        }
    }

	/* (non-Javadoc)
	 * @see personal.wuyi.jibernate.core.Dao#read(personal.wuyi.jibernate.query.Query)
	 */
	@Override
    @SuppressWarnings("unchecked")
    public <T extends Persisted> List<T> read(JQuery<T> query) {
        final EntityManager entityManager = getEntityManager();

        try {
            final Query jpaQuery = QueryConverter.getJpaQuery(entityManager, query);
            List<T> results = jpaQuery.getResultList();
            if(results == null) {
                results = new ArrayList<>(0);
            }
            return results;
        } finally {
            entityManager.close();
        }
    }

    /* (non-Javadoc)
     * @see personal.wuyi.jibernate.core.Dao#read(personal.wuyi.jibernate.query.Query, java.lang.String[])
     */
    @Override
    public List<List<?>> read(JQuery<? extends Persisted> query, String... fieldNames) {
        final EntityManager entityManager = getEntityManager();

        try {
            final Query jpaQuery = QueryConverter.getJpaQuery(entityManager, query, fieldNames);
            List<?> results = jpaQuery.getResultList();
            
            if(results == null) {
                return new ArrayList<>(0);
            } else {
                List<List<?>> list = new ArrayList<>();
                if(fieldNames.length == 1) {
                    for(Object result : results) {
                        List<Object> sublist = new ArrayList<>();
                        sublist.add(result);
                        list.add(sublist);
                    }
                } else {
                    for(Object result : results) {
                        list.add(Arrays.asList(((Object[]) result)));
                    }
                }
                return list;
            }
        } finally {
            entityManager.close();
        }
    }

    /* (non-Javadoc)
     * @see personal.wuyi.jibernate.core.Dao#count(personal.wuyi.jibernate.query.Query)
     */
    @Override
    public <T extends Persisted> long count(JQuery<T> query) {
        final EntityManager entityManager = getEntityManager();

        try {
            // make a copy since we are going to modify query
            query = ReflectUtil.copy(query);
            final Query jpaQuery = QueryConverter.getJpaQuery(entityManager, query, "COUNT(*)");
            return (Long) jpaQuery.getSingleResult();
        } finally {
            entityManager.close();
        }
    }

    /* (non-Javadoc)
     * @see personal.wuyi.jibernate.core.Dao#write(personal.wuyi.jibernate.core.Persisted)
     * 
     * @see  <a href="http://stackoverflow.com/questions/1069992/jpa-entitymanager-why-use-persist-over-merge">
     *           Why use persist() over merge()?
     *       </a>
     *       
     * @see  <a href="http://spitballer.blogspot.com/2010/04/jpa-persisting-vs-merging-entites.html">
     *           JPA: persisting vs. merging entites
     *       </a>
     */
    public <T extends Persisted> void write(T t) throws DatabaseOperationException  {
        final EntityManager entityManager = getEntityManager();

        try {
            entityManager.getTransaction().begin();

            // create new records, use persist()
            // update existing records, use merge()
            if(((ManagedEntity) t).getId() == null) {
                entityManager.persist(t);
            } else {
                entityManager.merge(t);
            }

            entityManager.getTransaction().commit();
        } catch(Exception e) {
            entityManager.getTransaction().rollback();
            logger.error("Error occurred when writing an object", e);
            throw new DatabaseOperationException("Error occurred when writing an object", e);
        } finally {
            entityManager.close();
        }
    }

    /* (non-Javadoc)
     * @see personal.wuyi.jibernate.core.Dao#write(java.util.List)
     * 
     * @see  <a href="http://stackoverflow.com/questions/1069992/jpa-entitymanager-why-use-persist-over-merge">
     *           Why use persist() over merge()?
     *       </a>
     *       
     * @see  <a href="http://spitballer.blogspot.com/2010/04/jpa-persisting-vs-merging-entites.html">
     *           JPA: persisting vs. merging entites
     *       </a>
     */
    public <T extends Persisted> void write(List<T> tList) throws DatabaseOperationException {
        final EntityManager entityManager = getEntityManager();

        try {
            entityManager.getTransaction().begin();

            for (T t : tList) {
                // create new records, use persist()
                // update existing records, use merge()                
            	if (((ManagedEntity) t).getId() == null) {
                    entityManager.persist(t);
                } else {
                    entityManager.merge(t);
                }
            }

            entityManager.getTransaction().commit();
        } catch(Exception e) {
            entityManager.getTransaction().rollback();
            logger.error("Error occurred when writing objects", e);
            throw new DatabaseOperationException("Error occurred when writing objects", e);
        } finally {
            entityManager.close();
        }
    }

    /* (non-Javadoc)
     * @see personal.wuyi.jibernate.core.Dao#delete(personal.wuyi.jibernate.core.Persisted)
     * 
     * @see  <a href="https://stackoverflow.com/questions/17027398/java-lang-illegalargumentexception-removing-a-detached-instance-com-test-user5">
     *           java.lang.IllegalArgumentException: Removing a detached instance com.test.User#5
     *       </a>
     */
    @Override
    public <T extends Persisted> void delete(T t) throws DatabaseOperationException {
        final EntityManager entityManager = getEntityManager();

        try {
            entityManager.getTransaction().begin();
            // Can not delete an entity which is not managed by entityManager
            // So check an entity is managed or not, if not, manage it first.
            entityManager.remove(entityManager.contains(t) ? t : entityManager.merge(t));
            entityManager.getTransaction().commit();
        } catch(Exception e) {
            entityManager.getTransaction().rollback();
            logger.error("Error occurred when deleting an object", e);
            throw new DatabaseOperationException("Error occurred when deleting an object", e);
        } finally {
            entityManager.close();
        }
    }
    
    /* (non-Javadoc)
     * @see personal.wuyi.jibernate.core.Dao#delete(personal.wuyi.jibernate.core.Persisted)
     * 
     * @see  <a href="https://stackoverflow.com/questions/17027398/java-lang-illegalargumentexception-removing-a-detached-instance-com-test-user5">
     *           java.lang.IllegalArgumentException: Removing a detached instance com.test.User#5
     *       </a>
     */
    @Override
    public <T extends Persisted> void delete(List<T> tList) throws DatabaseOperationException {
    		final EntityManager entityManager = getEntityManager();

        try {
            entityManager.getTransaction().begin();
            
            for (T t : tList) {
            	// Can not delete an entity which is not managed by entityManager
                // So check an entity is managed or not, if not, manage it first.
            	entityManager.remove(entityManager.contains(t) ? t : entityManager.merge(t));
            }
            
            entityManager.getTransaction().commit();
        } catch(Exception e) {
            entityManager.getTransaction().rollback();
            logger.error("Error occurred when deleting objects", e);
            throw new DatabaseOperationException("Error occurred when deleting objects", e);
        } finally {
            entityManager.close();
        }
    }
    
	/**
	 * Get an {@code EntityManager}.
	 * 
	 * @return  An {@code EntityManager}.
	 * 
     * @since   1.0
	 */
	protected EntityManager getEntityManager() {
		return getEntityManagerFactory().createEntityManager();
	}
	
	/**
	 * Get an {@code EntityManagerFactory}.
	 * 
	 * <p>{@code EntityManagerFactory} will be initialized once and reuse 
	 * after that.
	 * 
	 * @return  An {@code EntityManagerFactory}.
	 * 
     * @since   1.0
	 */
	protected EntityManagerFactory getEntityManagerFactory() {
		if (entityManagerFactory == null) {
			Map<String, Object> properties          = getProperties();
			PersistenceUnitInfo persistenceUnitInfo = getPersistUnitInfo();
			entityManagerFactory = new HibernatePersistenceProvider().createContainerEntityManagerFactory(persistenceUnitInfo, properties);
		}
		return entityManagerFactory;
	}
	
	/**
	 * Get the properties based on different types of {@code EntityManager}.
	 * 
	 * @return  The {@code HashMap} contains all the properties.
	 * 
     * @since   1.0
	 */
	private Map<String,Object> getProperties() {
        Map<String,Object> properties = new HashMap<>();

        properties.put(AvailableSettings.DIALECT,    getDialect());
        properties.put(AvailableSettings.DATASOURCE, getDataSource());

        return properties;
    }
	
	/**
	 * Get the info of the persistence unit based on the {@code EntityManager}.
	 * 
	 * @return  The {@code PersistenceUnitInfo}.
	 * 
	 * @since   1.0
	 */
	private PersistenceUnitInfo getPersistUnitInfo() {
		return new CommonPersistenceUnitInfo(getPersistenceUnit());
	}
	
    /* (non-Javadoc)
     * Open a {@code EntityManagerFactory}.
     * 
     * @see personal.wuyi.jibernate.core.Plugin#start()
     */
	@Override
    public void start() {
        getEntityManagerFactory();
    }

    /* (non-Javadoc)
     * Close the {@code EntityManagerFactory}.
     * 
     * @see personal.wuyi.jibernate.core.Plugin#stop()
     */
	@Override
    public void stop() {
        getEntityManagerFactory().close();
        entityManagerFactory = null;
    }
}
