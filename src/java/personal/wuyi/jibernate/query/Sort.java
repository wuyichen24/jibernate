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
 * @version 1.1
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

    public    String     getField()                      { return field;               }
    public    void       setField(String field)          { this.field = field;         }
    public    boolean    isAscending()                   { return ascending;           }
    public    void       setAscending(boolean ascending) { this.ascending = ascending; }
    protected List<Sort> getList()                       { return list;                }

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
            String newInput = input.trim();
            
            if(newInput.length() > 0) {
                String[] parsed = newInput.split(",");
                
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
    
    @Override
    public boolean equals(Object o) {
    	if (o == null) {
    		return false;
    	}
    	
    	if (!(o instanceof Sort)) {
    		return false;
    	}
    	
    	Sort s = (Sort) o;
    	
    	if (field == null) {
    		if (s.getField() != null) {
    			return false;
    		}
    	} else if (!field.equals(s.getField())) {
    		return false;
    	}
    	
    	if (ascending != s.isAscending()) {
    		return false;
    	}
    	
    	if (list == null) {
    		if (s.getList() != null) {
    			return false;
    		}
    	} else {
    		if (s.getList() == null) {
    			return false;
    		}
    		
    		if (list.size() != s.getList().size()) {
    			return false;
    		}
    		
    		for (int i = 0; i < list.size(); i++) {
    			if (!list.get(i).equals(s.getList().get(i))) {
    				return false;
    			}
    		}
    	}
    	
    	return true;
    }
}
