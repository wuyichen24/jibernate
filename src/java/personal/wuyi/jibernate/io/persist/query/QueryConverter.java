package personal.wuyi.jibernate.io.persist.query;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import personal.wuyi.jibernate.io.persist.core.Versioned;
import personal.wuyi.jibernate.io.persist.expression.Expression;
import personal.wuyi.jibernate.io.persist.expression.Subject;
import personal.wuyi.jibernate.io.persist.transformer.SearchExpressionTransformer;
import personal.wuyi.jibernate.io.persist.transformer.UriExpressionTransformer;
import personal.wuyi.jibernate.util.Md5;
import personal.wuyi.reflect.ReflectUtil;

/**
 * Converter for converting the our query to JPQL query
 *
 */
public class QueryConverter {
	/**
     * Convert our query to JPA query
     *
     * @param entityManager
     * @param transformedQuery
     * @return
     */
    public static javax.persistence.Query getJpaQuery(EntityManager entityManager, Query<?> query, String... select) {
    	Query<?> transformedQuery = transform(query);

        Class<?> persistedClass = transformedQuery.getPersistedClass();
        Expression criteria     = transformedQuery.getCriteria();
        Sort sort               = transformedQuery.getSort();
        boolean caseSensitive   = transformedQuery.isCaseSensitive();
        boolean distinct        = transformedQuery.isDistinct();
        Integer limit           = transformedQuery.getLimit();
        Integer offset          = transformedQuery.getOffset();

        // explicit JPQL is given precedent, otherwise convert query to JPQL
        String jpql = (transformedQuery instanceof EntityQuery) ? ((EntityQuery<?>)transformedQuery).getJpql() : null;
        if(jpql == null) {
            jpql = getJpql(persistedClass, criteria, sort, caseSensitive, distinct, select);
        }

        // create JPA query
        javax.persistence.Query jpaQuery = entityManager.createQuery(jpql);

        // set parameters
        if(criteria != null) {
            Map<String,Object> parameterMap = getParameterMap(persistedClass, criteria, caseSensitive);
            for(Map.Entry<String,Object> entry : parameterMap.entrySet()) {
                jpaQuery.setParameter(entry.getKey(), entry.getValue());
            }
        }

        // limit
        if(limit != null) {
            jpaQuery.setMaxResults(limit);
            if(offset != null) {
                jpaQuery.setFirstResult(offset);
            }
        }

        return jpaQuery;
    }
    
    /**
     * Handle any query conversion, normalization, etc., prior to evaluation.
     *
     * @param copiedQuery
     * @return
     */
    protected static Query<?> transform(Query<?> query) {
    	Query<?> copiedQuery = ReflectUtil.copy(query);

        // transform to vanilla SQL criteria
        Expression criteria = transform(copiedQuery.getCriteria());
        copiedQuery.setCriteria(criteria);

        // when querying for versioned objects, we implicitly filter and only retrieve head UNLESS query.history=true
        if(Versioned.class.isAssignableFrom(copiedQuery.getPersistedClass()) && !copiedQuery.isHistory()) {
        	// filter any historical revisions by adding "head" criteria
        	Expression headCriteria = new Expression("head", Expression.EQUAL, true);
        	if(criteria == null) {
        		copiedQuery.setCriteria(headCriteria);
        	} else {
        		copiedQuery.setCriteria(Expression.and(criteria, headCriteria));
            }
        }
        return copiedQuery;
    }
    
    /**
     * Transform any GH specific expression values into vanilla SQL
     * Example:
     *   ([A]STARTS_WITH "foo") => ([A] LIKE "%foo")
     *
     * @param expression
     * @return
     */
    protected static Expression transform(Expression expression) {
        UriExpressionTransformer uriExpressionTransformer = new UriExpressionTransformer();
        expression = uriExpressionTransformer.transform(expression);

        SearchExpressionTransformer searchExpressionTransformer = new SearchExpressionTransformer();
        expression =  searchExpressionTransformer.transform(expression);

        return expression;
    }
    
    /**
     * Generate parameter map from query criteria (this will be used to populate prepared statement)
     *
     * @param persistedClass
     * @param criteria
     * @return
     */
    protected static Map<String,Object> getParameterMap(Class<?> persistedClass, Expression criteria, boolean caseSensitive) {
        if( criteria== null ) {
            return null;
        }

        Map<String,Object> paramMap = new HashMap<>();

        // prefix will do a logical walk or tree, in this case order doesn't really matter though we just want to
        // grab unique subject/params and build param map from that
        criteria.prefix((node) -> {
            // should always be simple when doing prefix walk
            if(node instanceof Expression) {
                Expression subExpr = (Expression) node;
                String subject = subExpr.getSubject().getName();
                Object value = subExpr.getValue();
                String param = getJpqlParameter( persistedClass, subject, value );

                if(value != null) {
                    if(value instanceof String) {
                        if(caseSensitive == false) {
                            value = value.toString().toUpperCase();
                        }
                    } else if(value instanceof Object[]) {
                        value = Arrays.asList((Object[]) value);
                    }
                }

                // NULL values are not parameterized, they will be handled explicitly (e.g. IS NULL, IS NOT NULL)
                if( value != null ) {
                    paramMap.put(param, value);
                }
            }
        } );

        return paramMap;
    }
    
    /**
     * Generate JPA JPQL string from query values
     *
     * @param persistedClass
     * @param select
     * @param criteria
     * @param sort
     * @param caseSensitive
     * @param distinct
     *
     * @return
     */
    protected static String getJpql(Class<?> persistedClass, Expression criteria, Sort sort, boolean caseSensitive, boolean distinct, String... select) {
        String jpqlName = persistedClass.getSimpleName();
        String jpqlAlias = getAlias( persistedClass );

        // SELECT -> * or columns
        String jpqlSelect = getSelect(persistedClass, distinct, select);

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append( jpqlSelect ).append(" FROM ").append( jpqlName ).append(" ").append(jpqlAlias);

        // CRITERIA -> WHERE
        if (criteria != null) {
            // convert criteria to minimized form for ease of conversion
            criteria = criteria.minimized();
            String jpqlCriteria = generateJpqlWhereClause(persistedClass, criteria, caseSensitive);
            sb.append(" WHERE ").append(jpqlCriteria);
        }

        // SORT -> ORDER BY
        if(sort != null) {
            sb.append(" ORDER BY");

            List<Sort> sorts = sort.toList();
            int i = 0;
            for(Sort s : sorts) {
                sb.append(" ").append(jpqlAlias).append(".").append(s.getValue());
                if(!s.isAscending()) {
                     sb.append(" DESC");
                }

                if(i + 1 < sorts.size()) {
                    sb.append(",");
                }

                i++;
            }
        }

        return sb.toString();
    }
    
    /**
     * Build the select clause.
     *
     * For most queries this is just the "alias" (class short name), but for "count" queries, or cases where we have
     * specified an explicit select clause we need to modify with explicit alias.
     *
     * @param persistedClass
     * @param select
     * @param distinct
     *
     * @return
     */
    protected static String getSelect(Class<?> persistedClass, boolean distinct, String... select) {
        String alias = getAlias(persistedClass);

        if(select == null || select.length == 0) {
            return alias;
        } else {
            // qualify the select clause with the JPQL table alias
            boolean first = true;
            StringBuilder sb = new StringBuilder();
            for(String prop : select) {
                if(!first) {
                    sb.append(",");
                }

                if(prop.equalsIgnoreCase("COUNT(*)")) {
                    sb.append("COUNT(").append(alias).append(")");
                } else {
                    // select DISTINCT(c.name) from Customer c
                    if(distinct) {
                        sb.append("DISTINCT(");
                    }

                    sb.append( alias ).append(".").append( prop );

                    if(distinct) {
                        sb.append(")");
                    }
                }
                first = false;
            }
            return sb.toString();
        }
    }
    
    /**
     * Convert the expression into JPQL where clause
     *
     * @param persistedClass
     * @param expression
     * @return
     */
    protected static String generateJpqlWhereClause(Class<?> persistedClass, Expression expression, boolean caseSensitive) {
        if( expression == null ) {
            return null;
        }

        if (!expression.isCompound()) {
            return generateJpqlWhereClauseSingleCondition(persistedClass, expression.getSubject(), expression.getPredicate(), expression.getValue(), caseSensitive);
        } else {
            StringBuilder c = new StringBuilder();

            for (int i = 0; i < expression.size(); i++) {
                Expression child = expression.getSubExpression(i);
                if (child != null) {
                    String childClause = generateJpqlWhereClause(persistedClass, child, caseSensitive);
                    String op = expression.getOperator(i);
                    if (Expression.AND.equals(op)) {
                        c.append(" AND ");
                    } else if(Expression.OR.equals(op)){
                        c.append(" OR ");
                    }
                    c.append(childClause);
                }
            }

            if (c.length() == 0) {
                return null;
            }

            return c.toString();
        }
    }
    
    /**
     * Generate one condition in the JPQL where clause
     *
     * @param c
     * @param subject
     * @param predicate
     * @param value
     * @param caseSensitive
     * @return
     */
    protected static String generateJpqlWhereClauseSingleCondition(Class<?> c, Subject subject, String predicate, Object value, boolean caseSensitive)  {
    	StringBuilder sb = new StringBuilder();

    	// for criteria purposes, we should only ever care about subject name (value is for evaluation purposes only)
    	String name = getAlias(c) + "." + subject.getName();

    	// generate unique, value specific parameter name
    	String parameter = ":" + getJpqlParameter(c, subject.getName(), value);

    	// only ignore case if non-case sensitive and non-null text value
    	boolean ignoreCase = !caseSensitive && value != null && value instanceof String;
    	if(ignoreCase) {
    		name = "UPPER(" + name + ")";
    	}
    	sb.append(name);

    	if(value == null) {
    		if(Expression.NOT_EQUAL.equals(predicate)) {
    			sb.append(" IS NOT NULL");
    		} else {
    			sb.append(" IS NULL");
    		}
    	} else {
    		String operator = getJpqlOperator(predicate);
    		sb.append(" ").append(operator).append(" ");

    		// with IN statement we expect List or Array
    		if(value instanceof Iterable || value instanceof Object[]) {
    			sb.append("(").append(parameter).append(")");
    		} else {
    			sb.append(parameter);
    		}
    	}

    	return sb.toString();
    }
   
    /**
     * Generate a unique JPQL parameter name based on query class, subject, and value
     *
     * NOTE: we must account for value in parameter in order to handle OR clauses, for example:
     *  (([A] = "value1") || ([A] = "value2"))
     *
     * In such cases we cannon rely on subject name alone to guarantee variable uniqueness.  To solve this we generate an MD5 hash of value.toString()
     *
     * @param persistedClass
     * @param subject
     * @param value
     * @return
     */
    protected static String getJpqlParameter(Class<?> persistedClass, String subject, Object value) {
    	subject = subject.replaceAll("\\.", "_").toUpperCase();

    	StringBuilder sb = new StringBuilder();
    	sb.append( persistedClass.getSimpleName().toUpperCase() ).append( "_" ).append(subject);

    	// MD5 has value to guarantee subject/value pair uniqueness
    	if(value != null) {
    		try {
    			String hash = Md5.hash( value.toString() );
    			sb.append("_").append( hash );
    		} catch( Exception e ) {
    			
    		}
    	}

    	return sb.toString();
    }
   
    /**
     * Get SQL table alias for argument class
     *
     * @param c
     * @return
     */
    protected static String getAlias(Class<?> c) {
    	return c.getSimpleName().toLowerCase();
    }
   
   /**
    * Convert Expression predicate to equivalent JPQL operator.
    *
    * @param predicate
    * @return
    */
    protected static String getJpqlOperator(String predicate) {
    	if(Expression.EQUAL.equals(predicate)) {
    		return "=";
    	}
    	return predicate;	
    }
}
