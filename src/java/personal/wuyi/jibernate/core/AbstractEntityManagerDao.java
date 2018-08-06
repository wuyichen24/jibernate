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

abstract class AbstractEntityManagerDao {
	private EntityManagerFactory entityManagerFactory;
	
	abstract String     getDialect();
	abstract DataSource getDataSource();
	abstract String     getPersistenceUnit();
	
	@SuppressWarnings("unchecked")
    public <T extends Persisted> T read(Uri uri) {
        Object id = uri.getId();
        EntityManager entityManager = getEntityManager();

        try {
            return (T) entityManager.find(uri.getType(), id);
        } finally {
            entityManager.close();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Persisted> List<T> read(Query<T> query) {
        EntityManager entityManager = getEntityManager();

        try {
            javax.persistence.Query jpaQuery = QueryConverter.getJpaQuery(entityManager, query);
            List<T> results = jpaQuery.getResultList();
            if(results == null) {
                results = new ArrayList<>(0);  // return empty list instead of null
            }

            return results;
        } finally {
            entityManager.close();
        }

    }

    public List<List<?>> read(Query<? extends Persisted> query, String... select) {
        EntityManager entityManager = getEntityManager();

        try {
            javax.persistence.Query jpaQuery = QueryConverter.getJpaQuery(entityManager, query, select);
            List<?> results = jpaQuery.getResultList();
            
            if(results == null) {
                return new ArrayList<>(0);  // return empty list instead of null
            } else {
                List<List<?>> list = new ArrayList<>();
                // override JPA behavior which varies based on single select, or multi-select
                if(select.length == 1) {
                    for(Object result : results) {
                        // ASSERT result is a distinct primitive/wrapper value (e.g. String, Integer, Date, etc.)
                        List<Object> sublist = new ArrayList<>();
                        sublist.add(result);
                        list.add(sublist);
                    }
                } else {
                    for(Object result : results) {
                        // ASSERT result is a Object[]
                        list.add(Arrays.asList(((Object[]) result)));
                    }
                }

                return list;
            }
        } finally {
            entityManager.close();
        }
    }

    public <T extends Persisted> long count(Query<T> query) {
        EntityManager entityManager = getEntityManager();

        try {
            // make a copy since we are going to modify query
            query = ReflectUtil.copy(query);

            // for counts we just make an explicit select clause
            javax.persistence.Query jpaQuery = QueryConverter.getJpaQuery(entityManager, query, "COUNT(*)");
            long count = (Long) jpaQuery.getSingleResult();

            return count;
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
