package personal.wuyi.jibernate.query;

import java.util.ArrayList;
import java.util.List;

/**
 * Sort
 * 
 * @author Wuyi Chen
 */
public class Sort {
    private String     value     = null;
    private boolean    ascending = true;
    private List<Sort> list      = null;

    protected Sort() {

    }

    /**
     * Construct a {@code Sort}
     * 
     * @param value
     */
    public Sort(String value) {
        this.value = value;
    }


    /**
     * Construct a {@code Sort}
     * 
     * @param value
     * @param ascending
     */
    public Sort(String value, boolean ascending) {
        this.value = value;
        this.ascending = ascending;
    }

    public String  getValue()                      { return value;               }
    public void    setValue(String value)          { this.value = value;         }
    public boolean isAscending()                   { return ascending;           }
    public void    setAscending(boolean ascending) { this.ascending = ascending; }
    
    /**
     * Parse sort expression
     *
     * @param input
     * @return
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

    public Sort add(Sort sort) {
        // if non-cascading then convert to cascading sort
        if(this.isCascading() == false) {
            list = new ArrayList<>();

            Sort self = new Sort(this.value, this.ascending);
            this.value = null;
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

    public Sort add(String value) {
        return add(new Sort(value));
    }

    public Sort add(String value, boolean ascending) {
        return add(new Sort(value, ascending));
    }

    public List<Sort> toList() {
        if (isCascading()) {
            return list;
        } else {
            List<Sort> selfList = new ArrayList<>();
            selfList.add(this);
            return selfList;
        }
    }

    public boolean isCascading() {
        if (list == null || list.isEmpty()) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "";
    }
}
