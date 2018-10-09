package personal.wuyi.jibernate.query;

import java.util.ArrayList;
import java.util.List;

/**
 * The Sort class.
 * 
 * <p>This class is to store the sorting information of your query. You can 
 * sort on one field or multiple fields, like:
 * <pre>
 *    new Sort("firstname", true).and("gpa", false);
 * </pre>
 * 
 * @author  Wuyi Chen
 * @date    10/08/2018
 * @version 1.0
 * @since   1.0
 */
public class Sort {
    private String     field     = null;
    private boolean    ascending = true;
    private List<Sort> list      = null;

    /**
     * Construct a {@code Sort}.
     * 
     * <p>This method will use ascending order for the field.
     * 
     * @param  field
     *         The field needs to be sorted on.
     *         
     * @since   1.0
     */
    public Sort(String field) {
        this.field = field;
    }

    /**
     * Construct a {@code Sort}.
     * 
     * @param  field
     *         The field needs to be sorted on.
     *         
     * @param  ascending
     *         Sorting in ascending order or not.
     *         
     * @since   1.0
     */
    public Sort(String field, boolean ascending) {
        this.field     = field;
        this.ascending = ascending;
    }

    public String  getField()                      { return field;               }
    public void    setField(String field)          { this.field = field;         }
    public boolean isAscending()                   { return ascending;           }
    public void    setAscending(boolean ascending) { this.ascending = ascending; }
    
    /**
     * Parse sort expression.
     * 
     * <p>Different fields are separated by comma, and for each field, use "-" 
     * as descending order and use "+" as ascending order following a field 
     * name. The format looks like:
     * <pre>
     *   field1+,field2-,field3+,...
     * </pre>
     *
     * @param  input
     *         The input string.
     *         
     * @return  The {@code Sort} object.
     * 
     * @since   1.0
     */
    public static Sort parse(String input) {
        Sort sort = null;
        
        if(input != null) {
            input = input.trim();
            
            if(input.length() > 0) {
                String[] parsed = input.split(",");
                
                for(String value : parsed) {
                    value = value.trim();
                    boolean ascending = true;
                    if (value.endsWith("-")) {
                        value = value.substring(0, value.length() - 1);
                        ascending = false;
                    } else if(value.endsWith("+")) {
                        value = value.substring(0, value.length() - 1);
                    }

                    if (sort == null) {
                        sort = new Sort(value, ascending);
                    } else {
                        sort.add(value, ascending);
                    }
                }
            }
        }
        return sort;
    }

    /**
     * Add another {@code Sort} object into this {@code Sort} object.
     * 
     * @param  sort
     *         The {@code Sort} object.
     *         
     * @return  The new merged {@code Sort} object.
     * 
     * @since   1.0
     */
    public Sort add(Sort sort) {
        if(!this.isCascading()) {
            list = new ArrayList<>();

            Sort self = new Sort(this.field, this.ascending);
            this.field = null;
            this.ascending = true;

            list.add(self);
        }

        if(sort.isCascading()) {
            list.addAll(sort.toList());
        } else {
            list.add(sort);
        }
        return this;
    }

    /**
     * Add another sorting field into this {@code Sort} object.
     * 
     * <p>The new field will be sorted as ascending order.
     * 
     * @param  field
     *         The field needs to be added into this {@code Sort} object.
     *         
     * @return  The new merged {@code Sort} object.
     * 
     * @since   1.0
     */
    public Sort add(String field) {
        return add(new Sort(field));
    }

    /**
     * Add another sorting field into this {@code Sort} object.
     * 
     * @param  field
     *         The field needs to be added into this {@code Sort} object.
     *         
     * @param  ascending
     *         Sorting in ascending order or not.
     *         
     * @return  The new merged {@code Sort} object.
     * 
     * @since   1.0
     */
    public Sort add(String field, boolean ascending) {
        return add(new Sort(field, ascending));
    }

    /**
     * Return a list of {@code Sort} object.
     * 
     * <p>This method will separate each sorting field into different 
     * {@code Sort} objects.
     * 
     * @return  The list of {@code Sort} object.
     * 
     * @since   1.0
     */
    public List<Sort> toList() {
        if (isCascading()) {
            return list;
        } else {
            List<Sort> newList = new ArrayList<>();
            newList.add(this);
            return newList;
        }
    }

    /**
     * Check this {@code Sort} object is cascading (compound) or not. 
     * 
     * @return  {@code true} if this {@code Sort} object is cascading 
     *                       (compound);
     *          {@code false} otherwise.
     *          
     * @since   1.0
     */
    public boolean isCascading() {
        return !(list == null || list.isEmpty());
    }

    @Override
    public String toString() {
    		if (isCascading()) {
    			StringBuilder sb = new StringBuilder();
            for (Sort sort : list) {
            		if (list.get(list.size() - 1).equals(sort)) {
            			sb.append(getStringOfSimpleSort(sort));
            		} else {
            			sb.append(getStringOfSimpleSort(sort)).append(",");
            		}
            }
            return sb.toString();
        } else {
            return getStringOfSimpleSort(this);
        }
    }
    
    /**
     * Get the string of a simple {@code Sort} object.
     * 
     * @param  sort
     *         The simple {@code Sort} object.
     *         
     * @return  The string of a simple {@code Sort} object.
     * 
     * @since   1.0
     */
    public String getStringOfSimpleSort(Sort sort) {
    		return sort.getField() + (sort.isAscending() ? "+" : "-");
    }
}
