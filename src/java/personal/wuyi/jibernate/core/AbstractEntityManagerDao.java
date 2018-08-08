package personal.wuyi.jibernate.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import personal.wuyi.jibernate.exception.DatabaseOperationException;
import personal.wuyi.jibernate.query.Query;
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
abstract class AbstractEntityManagerDao {
	private EntityManagerFactory entityManagerFactory;
	
	private static Logger logger = LoggerFactory.getLogger(AbstractEntityManagerDao.class);
	
	/**
	 * Get the Hibernate dialect string corresponding to DB technology (MySQL, Oracle, etc.)
	 * 
	 * @return  The string of dialect.
	 */
	abstract String     getDialect();
	
	/**
     * Build the {@code DataSource} used by current {@code EntityManager} 
     * which is specific to DB technology (MySQL, Oracle, etc.),
     * host/environment and credentials.
     *
     * @return  The {@code DataSource}.
     */
	abstract DataSource getDataSource();
	
	/**
	 * Get the name of persistence unit.
	 * 
	 * @return  The name of persistence unit.
	 */
	abstract String     getPersistenceUnit();
	
	/**
	 * Query a single record from database. 
	 * 
	 * <p>For different types of data, it can define their way to identify 
	 * each unique record. Commonly, the combination of the table name and the 
	 * primary key for that table will be the unique identifier. The unique 
	 * identifier is the only way to retrieve a single record from database.
	 * 
	 * @param  uri
	 *         The {@code URI} to identify the single.
	 *         
	 * @return  A single record / object.
	 * 
     * @since   1.0
	 */
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

    /**
     * Query a list of records from database by a certain criteria.
     * 
     * <p>If there is no matched records for a query. This method will return 
     * a empty list.
     * 
     * @param  query
     *         The {@code Query} as criteria to limit the set of results.
     *     
     * @return  A list of the matched records.
     * 
     * @since   1.0
     */
    @SuppressWarnings("unchecked")
    public <T extends Persisted> List<T> read(Query<T> query) {
        final EntityManager entityManager = getEntityManager();

        try {
            final javax.persistence.Query jpaQuery = QueryConverter.getJpaQuery(entityManager, query);
            List<T> results = jpaQuery.getResultList();
            if(results == null) {
                results = new ArrayList<>(0);
            }
            return results;
        } finally {
            entityManager.close();
        }
    }

    /**
     * Query a list of records from database for only selected field(s).
     * 
     * <p>This method will return {@code List<List<?>>}. The element in the 
     * outer list is by record. The elements in the inner list 
     * ({@code List<?>}) are the values of the selected fields.
     * 
     * <p>For example, if you only select 2 fields and there are only 3 
     * matched records. The result will look like: 
     * <pre>
     * {
     *     {Record1Field1Value, Record1Field2Value},  
     *     {Record2Field1Value, Record2Field2Value}, 
     *     {Record3Field1Value, Record3Field2Value}
     * }
     * <pre>
     * 
     * <p>If there is no matched records for a query. This method will return 
     * a empty list.
     * 
     * @param  query
     *         The {@code Query} as criteria to limit the set of results.
     * 
     * @param  fieldNames
     *         The array of field names in Java class (not the column names in 
     *         database).
     *         
     * @return  The nested {@code List} of {@code List<?>}. The element in the 
     *          outer list is by record. The elements in the inner list 
     *          ({@code List<?>}) are the values of the selected fields.
     *          
     * @since   1.0
     */
    public List<List<?>> read(Query<? extends Persisted> query, String... fieldNames) {
        final EntityManager entityManager = getEntityManager();

        try {
            final javax.persistence.Query jpaQuery = QueryConverter.getJpaQuery(entityManager, query, fieldNames);
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

    /**
     * Count the number of matched records for a certain criteria.
     * 
     * @param  query
     *         The {@code Query} as criteria to limit the set of results.
     *         
     * @return  The number of matched records.
     * 
     * @since   1.0
     */
    public <T extends Persisted> long count(Query<T> query) {
        final EntityManager entityManager = getEntityManager();

        try {
            // make a copy since we are going to modify query
            query = ReflectUtil.copy(query);
            final javax.persistence.Query jpaQuery = QueryConverter.getJpaQuery(entityManager, query, "COUNT(*)");
            return (Long) jpaQuery.getSingleResult();
        } finally {
            entityManager.close();
        }
    }

    /**
     * Insert a new record or update a existing record to database.
     * 
     * @param  t
     *         The record needs to be inserted or updated.
     *         
     * @throws  DatabaseOperationException
     *          There is an error occurred when writing a record.
     * 
     * @see  <a href="http://stackoverflow.com/questions/1069992/jpa-entitymanager-why-use-persist-over-merge">
     *           Why use persist() over merge()?
     *       </a>
     *       
     * @see  <a href="http://spitballer.blogspot.com/2010/04/jpa-persisting-vs-merging-entites.html">
     *           JPA: persisting vs. merging entites
     *       </a>
     *       
     * @since   1.0
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

    /**
     * Insert a list of new records or update a list of existing records to 
     * database.
     * 
     * @param  tList
     *         The list of records needs to be inserted or updated.
     *         
     * @throws  DatabaseOperationException
     *          There is an error occurred when writing a record.
     * 
     * @see  <a href="http://stackoverflow.com/questions/1069992/jpa-entitymanager-why-use-persist-over-merge">
     *           Why use persist() over merge()?
     *       </a>
     *       
     * @see  <a href="http://spitballer.blogspot.com/2010/04/jpa-persisting-vs-merging-entites.html">
     *           JPA: persisting vs. merging entites
     *       </a>
     *       
     * @since   1.0
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

    /**
     * Delete an record from database.
     * 
     * <p>You can not remove an record which is not managed by the entity 
     * manager, so this method will check the record is managed or not. 
     * If not, that record will be managed first an then deleted.
     * 
     * @param  t
     *         The record needs to be deleted.
     *         
     * @throws  DatabaseOperationException
     *          There is an error occurred when deleting a record.
     *          
     * @see  <a href="https://stackoverflow.com/questions/17027398/java-lang-illegalargumentexception-removing-a-detached-instance-com-test-user5">
     *           java.lang.IllegalArgumentException: Removing a detached instance com.test.User#5
     *       </a>
     *          
     * @since   1.0
     */
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
    
    /**
     * Delete a list of records from database.
     * 
     * <p>You can not remove an record which is not managed by the entity 
     * manager, so this method will check the record is managed or not. 
     * If not, that record will be managed first an then deleted.
     * 
     * @param  tList
     *         The list of the records needs to be deleted.
     *         
     * @throws  DatabaseOperationException
     *          There is an error occurred when deleting a list of records.
     *          
     * @see  <a href="https://stackoverflow.com/questions/17027398/java-lang-illegalargumentexception-removing-a-detached-instance-com-test-user5">
     *           java.lang.IllegalArgumentException: Removing a detached instance com.test.User#5
     *       </a>
     *          
     * @since   1.0
     */
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
	
    /**
     * Open a {@code EntityManagerFactory}.
     * 
	 * @since   1.0
     */
    public void start() {
        getEntityManagerFactory();
    }

    /**
     * Close the {@code EntityManagerFactory}.
     * 
	 * @since   1.0
     */
    public void stop() {
        getEntityManagerFactory().close();
        entityManagerFactory = null;
    }
}
