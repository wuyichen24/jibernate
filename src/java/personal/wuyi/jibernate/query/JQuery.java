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

import com.google.common.base.Preconditions;

import personal.wuyi.jibernate.entity.Persisted;
import personal.wuyi.jibernate.expression.Expression;

/**
 * Generic database query class.
 * 
 * @author  Wuyi Chen
 * @date    09/19/2018
 * @version 1.0
 * @since   1.0
 */
public class JQuery<E extends Persisted> {
    public static final String IN   = "IN";
    public static final String LIKE = "LIKE";

    protected Class<E>   clazz;
    protected Expression criteria;
    protected Sort       sort;
    protected Integer    offset;
    protected Integer    limit;
    protected boolean    caseSensitive = true;  // queries are case-sensitive by default
    protected boolean    distinct      = false; // only applicable in conjunction with "select"
    protected boolean    history       = false; // only applicable to versioned objects, by default persist service will filter historical versions and only retrieve current/head version unless history explicitly set to true

    protected JQuery() {}

    /**
     * Constructs a {@code Query}.
     *
     * @param  clazz
     *         The class of an entity needs to be queried.
     *         
     * @since   1.0
     */
    public JQuery(Class<E> clazz) {
        this.clazz = clazz;
    }

    public Class<E>   getPersistedClass()                     { return clazz;                                   }
    public void       setPersistedClass(Class<E> clazz)       { this.clazz = clazz;                             }
    public Expression getCriteria()                           { return criteria;                                }
    public Expression setCriteria(Expression criteria)        { this.criteria = criteria; return this.criteria; }
    public Sort       getSort()                               { return sort;                                    }
    public void       setSort(Sort sort)                      { this.sort = sort;                               }
    public Integer    getOffset()                             { return offset;                                  }
    public void       setOffset(Integer offset)               { this.offset = offset;                           }
    public Integer    getLimit()                              { return limit;                                   }
    public boolean    isCaseSensitive()                       { return caseSensitive;                           }
    public void       setCaseSensitive(boolean caseSensitive) { this.caseSensitive = caseSensitive;             }
    public boolean    isDistinct()                            { return distinct;                                }
    public void       setDistinct(boolean distinct)           { this.distinct = distinct;                       }
    public boolean    isHistory()                             { return history;                                 }
    public void       setHistory(boolean history)             { this.history = history;                         }

    /**
     * Set the limit of a query.
     * 
     * @param  limit
     *         The limit of records in the result list of a query.
     *         
     * @since   1.0
     */
    public void setLimit(Integer limit) {
    	Preconditions.checkArgument(limit > 0, "The number for the limit should be greater than 0.");
    	this.limit = limit;
    }
    
    /**
     * Set an simple expression as the criteria of the query.
     * 
     * @param  subject
     *         The subject of an expression.
     *         
     * @param  operator
     *         The operator of an expression.
     *         
     * @param  value
     *         The value of an expression.
     *         
     * @return  The new constructed expression.
     * 
     * @since   1.0
     */
    public Expression setCriteria(String subject, String operator, Object value) {
        return setCriteria(new Expression(subject, operator, value));
    }
    
    /**
     * Set the sorting logic based one or more columns.
     * 
     * <p>The parameters are the fields needs to be sorted and the order. 
     * <ul>
     *   <li>For ascending order, you need to add "+" at the end of the field name. 
     *   <li>For descending order, you need to "-" at the end of the field name.
     * </ul>
     * 
     * <p>There is an example:
     * <pre>
     * {@code 
     * query.setSort("age+");     // ascending order on age
     * query.setSort("score-");   // descending order on score
     * }
     * </pre>
     * 
     * @param  sorts
     *         The list of fields needs to be sorted and the order (ascending/descending).
     *         
     * @since   1.0
     */
    public void setSortTemplate(String... sorts) {
        Sort newSort = null;

        for(String s : sorts) {
            boolean ascending = true;
            s = s.trim();
            if(s.endsWith("+")) {
                s = s.substring(0, s.length() - 1 );
            } else if(s.endsWith("-")) {
                s = s.substring(0, s.length() - 1);
                ascending = false;
            }

            if(newSort == null) {
                newSort = new Sort(s, ascending);
            } else {
                newSort.add(s, ascending);
            }
        }
        this.sort = newSort;
    }
    
    @Override
    public String toString() {
        return "";
    }
}
