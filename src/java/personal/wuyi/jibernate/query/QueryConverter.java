/*
 * Copyright 2018 Wuyi Chen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package personal.wuyi.jibernate.query;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.common.base.Joiner;

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
	private QueryConverter() {}
	
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
        
        transformedQuery.setSort(query.getSort()); 

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
        }
        
        if(offset != null) {
            jpaQuery.setFirstResult(offset);
        }

        return jpaQuery;
    }
    
    /**
     * Normalize the query.
     * 
     * <p>First, this method will transform the criteria (expression) of the 
     * query from the project-specific grammar into the vanilla SQL grammar. 
     * Second for versioned objects, it implicitly filter and only retrieve 
     * head.
     *
     * @param  query
     *         The query needs to be normalized.
     * 
     * @return  The normalized query.
     * 
     * @since   1.0
     */
    protected static JQuery<?> transform(JQuery<?> query) {
    	JQuery<?> copy = ReflectUtil.copy(query);

        copy.setCriteria(transform(copy.getCriteria()));

        if(Versioned.class.isAssignableFrom(copy.getPersistedClass()) && !copy.isHistory()) {
        	Expression headCriteria = new Expression("head", Expression.EQUAL, true);
        	if(copy.getCriteria() == null) {
        		copy.setCriteria(headCriteria);
        	} else {
        		copy.setCriteria(Expression.and(copy.getCriteria(), headCriteria));
            }
        }
        return copy;
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
        Expression expr1 = (new UriExpressionTransformer()).transform(expression);
        Expression expr2 = (new SearchExpressionTransformer()).transform(expr1);
        return expr2;
    }
    
    /**
     * Get JPQL query statement.
     * 
     * <p>If the query object is already an instance of {@code EntityQuery}, 
     * so get the JPQL statement directly; Otherwise, manually build the JPQL 
     * statement.
     * 
     * @param  query
     *         The query needs to be processed.
     * 
     * @param  fields
     *         The fields need to be populated.
     *         
     * @return  The JPQL statement.
     * 
     * @since   1.0
     */
    protected static String getJpqlStatement(JQuery<?> query, String... fields) {
    	Class<?>   clazz         = query.getPersistedClass();
        Expression criteria      = query.getCriteria();
        Sort       sort          = query.getSort();
        boolean    caseSensitive = query.isCaseSensitive();
        boolean    distinct      = query.isDistinct();
    	
        if (query instanceof EntityQuery && ((EntityQuery<?>)query).getJpql() != null) {
        	return ((EntityQuery<?>)query).getJpql();
        } else {
        	return buildJpqlStatement(clazz, criteria, sort, caseSensitive, distinct, fields);
        }
    }
    
    /**
     * Build JPQL statement.
     *
     * @param  clazz
     *         The persisted class.
     *         
     * @param  fields
     *         The fields need to be queried.
     *         
     * @param  criteria
     *         The criteria (expression) of the query.
     * 
     * @param  sort
     *         The sorting option.
     * 
     * @param  caseSensitive
     *         Is case sensitive or not.
     *         
     * @param  distinct
     *         Need to see distinct values of a certain field.
     *
     * @return  The JPQL statement.
     * 
     * @since   1.0
     */
    protected static String buildJpqlStatement(Class<?> clazz, Expression criteria, Sort sort, boolean caseSensitive, boolean distinct, String... fields) {        
        String select = buildBasicSelectStatement(clazz, distinct, fields);
        
        String where = null;
        if (criteria != null) {
            where = buildWhereClause(clazz, criteria, caseSensitive);
        }

        String orderBy = null;
        if(sort != null) {
        	orderBy = buildOrderByClause(clazz, sort);
        }
        
        return Joiner.on(" ").skipNulls().join(Arrays.asList(select, where, orderBy));
    }
    
    /**
     * Generate the parameter map from the criteria of the query.
     * 
     * <p>This method will traverse the expression tree of the criteria and 
     * add the key-value pair of each node into the map.
     * 
     * <p>This key will be the unique parameter based on the persisted class, 
     * subject and value of the query. The value will be the value of the 
     * query.
     *
     * @param  clazz
     *         The persisted class.
     *         
     * @param  criteria
     *         The criteria of the query.
     * 
     * @return  The map of parameters.
     * 
     * @since   1.0
     */
    protected static Map<String,Object> getParameterMap(Class<?> clazz, Expression criteria, boolean caseSensitive) {
        if(criteria == null) {
            return null;
        }

        Map<String,Object> paramMap = new HashMap<>();

        criteria.prefix(node -> {
            if(node instanceof Expression) {
                Expression subExpr = (Expression) node;
                String     subject = subExpr.getSubject().getName();
                Object     value   = subExpr.getValue();
                String     param   = getJpqlParameter(clazz, subject, value);

                if(value != null) {
                    if(value instanceof String) {
                        if(!caseSensitive) {
                            value = value.toString().toUpperCase();
                        }
                    } else if(value instanceof Object[]) {
                        value = Arrays.asList((Object[]) value);
                    }
                
                    paramMap.put(param, value);
                }
            }
        });

        return paramMap;
    }
    
    /**
     * Build a basic select statement.
     * 
     * <p>This basic select statement only has "SELECT" and "FROM" keywords.
     * 
     * @param  clazz
     *         The persisted class.
     *         
     * @param  distinct
     *         Need distinct values or not.
     *         
     * @param  fields
     *         The fields need to be queried.
     *         
     * @return  The basic select statement.
     * 
     * @since   1.0
     */
    protected static String buildBasicSelectStatement(Class<?> clazz, boolean distinct, String... fields) {
    	String tableName  = clazz.getSimpleName();
        String tableAlias = getAlias(clazz);
        
    	String selectClause = buildSelectClause(clazz, distinct, fields);
    	return Joiner.on(" ").join(Arrays.asList("SELECT", selectClause, "FROM", tableName, tableAlias));
    }
    
    /**
     * Build a select clause.
     *
     * For most queries this is just the "alias" (class short name), but for 
     * "count" queries, or cases where we have specified an explicit select 
     * clause we need to modify with explicit alias.
     *
     * @param  clazz
     *         The persisted class.
     *         
     * @param  distinct
     *         Need distinct values or not.
     *         
     * @param  fields
     *         The fields need to be queried.
     *
     * @return  The select clause.
     * 
     * @since   1.0
     */
    protected static String buildSelectClause(Class<?> clazz, boolean distinct, String... fields) {
        String alias = getAlias(clazz);

        if(fields == null || fields.length == 0) {
            return alias;
        } else {
            boolean first = true;
            StringBuilder sb = new StringBuilder();
            for(String field : fields) {
                if(!first) {
                    sb.append(",");
                }

                if(field.equalsIgnoreCase("COUNT(*)")) {
                    sb.append("COUNT(").append(alias).append(")");
                } else {
                    if(distinct) {
                        sb.append("DISTINCT(");
                    }

                    sb.append(alias).append(".").append(field);

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
     * Build where clause.
     * 
     * <p>This method will concatenate the "WHERE" keyword with the where expression. 
     * 
     * @param  clazz
     *         The persisted class.
     * 
     * @param  criteria
     *         The criteria of the query.
     * 
     * @param  caseSensitive
     *         Is case sensitive or not.
     *         
     * @return  The where clause.
     * 
     * @since   1.0
     */
    protected static String buildWhereClause(Class<?> clazz, Expression criteria, boolean caseSensitive) {
    	criteria = criteria.minimized();
    	String whereExpression = buildWhereExpression(clazz, criteria, caseSensitive);
    	return Joiner.on(" ").join("WHERE", whereExpression);
    }
    
    /**
     * Build a where expression (by the criteria of the query).
     * 
     * <p>This method will loop through the current expression and its 
     * sub-expressions recursively and generate the where expression.
     *
     * @param  clazz
     *         The persisted class.
     *         
     * @param  expression
     *         The criteria of the query.
     * 
     * @return  The where expression.
     * 
     * @since   1.0
     */
    protected static String buildWhereExpression(Class<?> clazz, Expression expression, boolean caseSensitive) {        
        if (!expression.isCompound()) {
        	return buildWhereExpressionForSimpleExpression(clazz, expression.getSubject(), expression.getOperator(), expression.getValue(), caseSensitive);
        } else {
            return buildWhereExpressionForCompoundExpression(clazz, expression, caseSensitive);
        }
    }
    
    /**
     * Build a where expression for a simple expression.
     *
     * @param  clazz
     *         The persisted class.
     * 
     * @param  subject
     *         The subject of the expression.
     * 
     * @param  operator
     *         The operator of the expression.
     * 
     * @param  value
     *         The value of the expression.
     * 
     * @param  caseSensitive
     *         Is case sensitive or not.
     * 
     * @return  The where expression.
     * 
     * @since   1.0
     */
    protected static String buildWhereExpressionForSimpleExpression(Class<?> clazz, Subject subject, String operator, Object value, boolean caseSensitive)  {
    	StringBuilder sb = new StringBuilder();

    	String name = getAlias(clazz) + "." + subject.getName();
    	boolean ignoreCase = !caseSensitive && value != null && value instanceof String;
    	if(ignoreCase) {
    		name = "UPPER(" + name + ")";
    	}
    	sb.append(name);

    	if(value == null) {
    		if(Expression.NOT_EQUAL.equals(operator)) {
    			sb.append(" IS NOT NULL");
    		} else {
    			sb.append(" IS NULL");
    		}
    	} else {
    		String optr = getJpqlOperator(operator);
    		sb.append(" ").append(optr).append(" ");
    		
    		String parameter = ":" + getJpqlParameter(clazz, subject.getName(), value);

    		if(value instanceof Iterable || value instanceof Object[]) {
    			if (!Expression.IN.equals(optr)) {
    				throw new IllegalArgumentException("You are passing multiple values into Expression, the operator must be Expression.IN");
    			}
    			sb.append("(").append(parameter).append(")");
    		} else {
    			if (Expression.IN.equals(optr)) {
    				throw new IllegalArgumentException("You are passing single value into Expression, the operator can not be Expression.IN");
    			}
    			sb.append(parameter);
    		}
    	}

    	return sb.toString();
    }
    
    /**
     * Build a where expression for a compound expression.
     * 
     * @param  clazz
     *         The persisted class.
     *         
     * @param  expression
     *         The criteria of the query.
     * 
     * @param  caseSensitive
     *         Is case sensitive or not.
     * 
     * @return  The where expression.
     * 
     * @since   1.0
     */
    protected static String buildWhereExpressionForCompoundExpression(Class<?> clazz, Expression expression, boolean caseSensitive) {
    	StringBuilder sb = new StringBuilder();

        for (int i = 0; i < expression.getNumberOfSubExpression(); i++) {
            Expression subExpr = expression.getSubExpression(i);
            if (subExpr != null) {
                String subExprStr = buildWhereExpression(clazz, subExpr, caseSensitive);
                String optr = expression.getOperator(i);
                if (Expression.AND.equals(optr)) {
                    sb.append(" AND ");
                } else if(Expression.OR.equals(optr)){
                    sb.append(" OR ");
                }
                sb.append(subExprStr);
            }
        }

        if (sb.length() == 0) {
            return null;
        }

        return sb.toString();
    }
    
    /**
     * Build a order by clause.
     * 
     * @param  clazz
     *         The persisted class.
     * 
     * @param  sort
     *         The sorting option.
     * 
     * @return  The order by clause.
     * 
     * @since   1.0
     */
    protected static String buildOrderByClause(Class<?> clazz, Sort sort) {
    	String tableAlias = getAlias(clazz);
  
    	StringBuilder sb = new StringBuilder();
    	
    	sb.append("ORDER BY");

        List<Sort> sorts = sort.toList();
        int i = 0;
        for(Sort s : sorts) {
            sb.append(" ").append(tableAlias).append(".").append(s.getField());
            if(!s.isAscending()) {
                 sb.append(" DESC");
            }

            if(i + 1 < sorts.size()) {
                sb.append(",");
            }

            i++;
        }
        
        return sb.toString();
    }
   
    /**
     * Generate a unique JPQL parameter name based on the persisted class, 
     * subject, and value.
     * 
     * <p>The format of JPQL parameter will be looked like:
     * <pre>
     *     [class name]_[subject name]_[value in MD5]
     * </pre>
     *
     * @param  clazz
     *         The persisted class.
     *       
     * @param  subject
     *         The subject of the expression.
     * 
     * @param  value
     *         The value of the expression.
     * 
     * @return  The unique JPQL parameter.
     * 
     * @since   1.0
     */
    protected static String getJpqlParameter(Class<?> clazz, String subject, Object value) {
    	String formattedSubject = subject.replaceAll("\\.", "_").toUpperCase();

    	StringBuilder sb = new StringBuilder();
    	sb.append(clazz.getSimpleName().toUpperCase()).append("_").append(formattedSubject);

    	if(value != null) {
    		try {
    			String hash = Md5.hash(value.toString());
    			sb.append("_").append(hash);
    		} catch(Exception e) {
    			// TODO
    		}
    	}

    	return sb.toString();
    }
   
    /**
     * Get the alias of a class.
     *
     * @param  clazz
     *         The class needs to be gotten the alias.
     *         
     * @return  The alias of a class.
     * 
     * @since   1.0
     */
    protected static String getAlias(Class<?> clazz) {
    	return clazz.getSimpleName().toLowerCase();
    }
   
   /**
    * Convert the project-specific operator to JPQL operator.
    *
    * @param  operator
    *         The operator needs to be converted.
    *         
    * @return  The converted JPQL operator.
    * 
    * @since   1.0
    */
    protected static String getJpqlOperator(String operator) {
    	if(Expression.EQUAL.equals(operator)) {
    		return "=";
    	}
    	return operator;	
    }
}
