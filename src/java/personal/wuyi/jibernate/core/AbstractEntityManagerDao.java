package personal.wuyi.jibernate.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.jpa.HibernatePersistenceProvider;

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
	
	abstract String     getDialect();
	abstract DataSource getDataSource();
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

    public <T extends Persisted> void write(T t)  {
        EntityManager entityManager = getEntityManager();

        try {
            entityManager.getTransaction().begin();

            // TODO http://stackoverflow.com/questions/1069992/jpa-entitymanager-why-use-persist-over-merge
            if(((ManagedEntity) t).getId() == null) {
                entityManager.persist(t);
            } else {
                entityManager.merge(t);
            }

            entityManager.getTransaction().commit();
        } catch(Exception e) {
            entityManager.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    public <T extends Persisted> void write(List<T> tList) {
        EntityManager entityManager = getEntityManager();

        try {
            entityManager.getTransaction().begin();

            for (T t : tList) {
                // TODO http://stackoverflow.com/questions/1069992/jpa-entitymanager-why-use-persist-over-merge
                if (((ManagedEntity) t).getId() == null) {
                    entityManager.persist(t);
                } else {
                    entityManager.merge(t);
                }
            }

            entityManager.getTransaction().commit();
        } catch(Exception e) {
            entityManager.getTransaction().rollback();
            // TODO Add logging
        } finally {
            entityManager.close();
        }
    }

    public <T extends Persisted> void delete(T persisted) {
        EntityManager entityManager = getEntityManager();

        try {
            entityManager.getTransaction().begin();
            entityManager.remove(persisted);
            entityManager.getTransaction().commit();
        } catch(Exception e) {
            entityManager.getTransaction().rollback();
            // TODO Add logging
        } finally {
            entityManager.close();
        }
    }
    
	protected EntityManager getEntityManager() {
		return getEntityManagerFactory().createEntityManager();
	}
	
	protected EntityManagerFactory getEntityManagerFactory() {
		if (entityManagerFactory == null) {
			Map<String, Object> properties = getProperties();
			PersistenceUnitInfo persistenceUnitInfo = getPersistUnitInfo();
			entityManagerFactory = new HibernatePersistenceProvider().createContainerEntityManagerFactory(persistenceUnitInfo, properties);
		}
		return entityManagerFactory;
	}
	
	private Map<String,Object> getProperties() {
        Map<String,Object> properties = new HashMap<>();

        properties.put("hibernate.dialect", getDialect());

        // use programmatic DataSource builder (instead of normal JPA persistence unit configuration)
        properties.put( AvailableSettings.DATASOURCE, getDataSource());

        // JPA spec does not support dynamic class discovery so we use Hibernate feature to "discover" for us (which works but is kinda a hack)
        Set<Class<?>> classes = getEntityClasses();
        if(classes != null) {
            // convert set to list expected by hibernate
            properties.put( "hibernate.ejb.loaded.classes", new ArrayList<>(classes));
        }

        return properties;
    }
	
	private Set<Class<?>> getEntityClasses() {
		try {
			return ReflectUtil.getPackageClasses("personal.wuyi.autostock.entity", true);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		
		return null;
    }
	
	private PersistenceUnitInfo getPersistUnitInfo() {
		return new CommonPersistenceUnitInfo(getPersistenceUnit());
	}
}
