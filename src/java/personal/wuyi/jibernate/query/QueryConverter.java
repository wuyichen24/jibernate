package personal.wuyi.jibernate.query;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import personal.wuyi.jibernate.entity.Versioned;
import personal.wuyi.jibernate.expression.Expression;
import personal.wuyi.jibernate.expression.Subject;
import personal.wuyi.jibernate.transformer.SearchExpressionTransformer;
import personal.wuyi.jibernate.transformer.UriExpressionTransformer;
import personal.wuyi.jibernate.util.Md5;
import personal.wuyi.reflect.ReflectUtil;

/**
 * The static converter class for converting {@code JQuery} (project query 
 * object) to JPQL query.
 * 
 * @author  Wuyi Chen
 * @date    09/25/2018
 * @version 1.0
 * @since   1.0
 */
public class QueryConverter {
	/**
     * Convert {@code JQuery} (project query object) to JPQL query.
     *
     * @param  entityManager
     *         The entity manager object.
     *         
     * @param  query
     *         The project-level query object.
     *         
     * @param  fields
     *         The fields needs to be queried from database.
     *   
     * @return  The JPQL query.
     * 
     * @since   1.0
     */
    public static Query getJpaQuery(EntityManager entityManager, JQuery<?> query, String... fields) {
    	JQuery<?> transformedQuery = transform(query);

        Class<?>   clazz         = transformedQuery.getPersistedClass();
        Expression criteria      = transformedQuery.getCriteria();
        boolean    caseSensitive = transformedQuery.isCaseSensitive();
        Integer    limit         = transformedQuery.getLimit();
        Integer    offset        = transformedQuery.getOffset();

        String jpqlStatement = getJpqlStatement(transformedQuery, fields);
        Query  jpaQuery      = entityManager.createQuery(jpqlStatement);

        if(criteria != null) {
            Map<String,Object> parameterMap = getParameterMap(clazz, criteria, caseSensitive);
            for(Entry<String,Object> entry : parameterMap.entrySet()) {
                jpaQuery.setParameter(entry.getKey(), entry.getValue());
            }
        }

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
    protected static JQuery<?> transform(JQuery<?> query) {
    	JQuery<?> copiedQuery = ReflectUtil.copy(query);

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
     * Transform an expression in this project-specific grammar into the 
     * vanilla SQL grammar.
     * 
     * <p>This method will do 2 transforming works:
     * <ul>
     *   <li>Transform URI expression:
     *     <pre>
     *       Expression("uri","=","/personal/wuyi/jibernate/entity/Student/27") ==> Expression("id", "=", "27")
     *     </pre>
     *   <li>Transform wild-card search:
     *     <pre>
     *       START_WITH 'ABC' ==> LIKE 'ABC%'
     *       END_WITH 'ABC'   ==> LIKE '%ABC'
     *       CONTAINS 'ABC'   ==> LIKE '%ABC%'
     *     </pre>
     * </ul>
     *
     * @param  expression
     *         The expression needs to be transformed.
     *         
     * @return  The transformed expression.
     * 
     * @since   1.0
     */
    protected static Expression transform(Expression expression) {
        expression = (new UriExpressionTransformer()).transform(expression);
        expression = (new SearchExpressionTransformer()).transform(expression);
        return expression;
    }
    
    /**
     * Get JPQL query statement.
     * 
     * <p>If the query object is already an instance of {@code EntityQuery}, 
     * so get the JPQL statement directly; Otherwise, manually build the JPQL 
     * statement.
     * 
     * @param  transformedQuery
     * @param  fields
     * @return
     */
    protected static String getJpqlStatement(JQuery<?> transformedQuery, String... fields) {
    	Class<?>   clazz         = transformedQuery.getPersistedClass();
        Expression criteria      = transformedQuery.getCriteria();
        Sort       sort          = transformedQuery.getSort();
        boolean    caseSensitive = transformedQuery.isCaseSensitive();
        boolean    distinct      = transformedQuery.isDistinct();
    	
        if (transformedQuery instanceof EntityQuery) {
        	return ((EntityQuery<?>)transformedQuery).getJpql();
        } else {
        	return buildJpqlStatement(clazz, criteria, sort, caseSensitive, distinct, fields);
        }
    }
    
    /**
     * Generate JPA JPQL string from query values
     *
     * @param persistedClass
     * @param selects
     * @param criteria
     * @param sort
     * @param caseSensitive
     * @param distinct
     *
     * @return
     */
    protected static String buildJpqlStatement(Class<?> persistedClass, Expression criteria, Sort sort, boolean caseSensitive, boolean distinct, String... selects) {
        String jpqlName = persistedClass.getSimpleName();
        String jpqlAlias = getAlias( persistedClass );

        // SELECT -> * or columns
        String jpqlSelect = getSelect(persistedClass, distinct, selects);

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
     * Build the select clause.
     *
     * For most queries this is just the "alias" (class short name), but for "count" queries, or cases where we have
     * specified an explicit select clause we need to modify with explicit alias.
     *
     * @param persistedClass
     * @param selects
     * @param distinct
     *
     * @return
     */
    protected static String getSelect(Class<?> persistedClass, boolean distinct, String... selects) {
        String alias = getAlias(persistedClass);

        if(selects == null || selects.length == 0) {
            return alias;
        } else {
            // qualify the select clause with the JPQL table alias
            boolean first = true;
            StringBuilder sb = new StringBuilder();
            for(String prop : selects) {
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
            return generateJpqlWhereClauseSingleCondition(persistedClass, expression.getSubject(), expression.getOperator(), expression.getValue(), caseSensitive);
        } else {
            StringBuilder c = new StringBuilder();

            for (int i = 0; i < expression.getNumberOfSubExpression(); i++) {
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
