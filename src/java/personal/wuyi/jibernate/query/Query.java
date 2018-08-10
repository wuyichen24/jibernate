package personal.wuyi.jibernate.query;

import personal.wuyi.jibernate.entity.Persisted;
import personal.wuyi.jibernate.expression.Expression;

/**
 * Query
 * 
 * @author Wuyi Chen
 */
public class Query<E extends Persisted> {
    public static final String IN = "IN";
    public static final String LIKE = "LIKE";

    protected Class<E>   persistedClass;
    protected Expression criteria;
    protected Sort       sort;
    protected Integer    offset;
    protected Integer    limit;
    protected boolean    caseSensitive = true;  // queries are case-sensitive by default
    protected boolean    distinct = false;      // only applicable in conjunction with "select"
    protected boolean    history = false;       // only applicable to versioned objects, by default persist service will filter historical versions and only retrieve current/head version unless history explicitly set to true

    protected Query() {}

    /**
     * Construct a {@code Query}
     *
     * @param persistedClass
     */
    public Query(Class<E> persistedClass) {

        this.persistedClass = persistedClass;
    }

    /**
     * Construct a {@code Query}
     *
     * @param query
     */
    public Query(Query<E> query) {

    }

    public Class<E>   getPersistedClass()                        { return persistedClass;                          }
    public void       setPersistedClass(Class<E> persistedClass) { this.persistedClass = persistedClass;           }
    public Expression getCriteria()                              { return criteria;                                }
    public Expression setCriteria(Expression criteria)           { this.criteria = criteria; return this.criteria; }
    public Sort       getSort()                                  { return sort;                                    }
    public void       setSort(Sort sort)                         { this.sort = sort;                               }
    public Integer    getOffset()                                { return offset;                                  }
    public void       setOffset(Integer offset)                  { this.offset = offset;                           }
    public Integer    getLimit()                                 { return limit;                                   }
    public void       setLimit(Integer limit)                    { this.limit = limit;                             }
    public boolean    isCaseSensitive()                          { return caseSensitive;                           }
    public void       setCaseSensitive(boolean caseSensitive)    { this.caseSensitive = caseSensitive;             }
    public boolean    isDistinct()                               { return distinct;                                }
    public void       setDistinct(boolean distinct)              { this.distinct = distinct;                       }
    public boolean    isHistory()                                { return history;                                 }
    public void       setHistory(boolean history)                { this.history = history;                         }

    public Expression setCriteria(String subject, String predicate, Object value) {
        return setCriteria(new Expression(subject, predicate, value));
    }
    
    public void setSort(String... sorts) {
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
