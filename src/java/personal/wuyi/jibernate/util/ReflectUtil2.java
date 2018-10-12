package personal.wuyi.jibernate.util;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import personal.wuyi.reflect.ReflectUtil;

/**
 * @author wuyichen
 *
 */
public class ReflectUtil2 {
	/**
	 * Check 2 general objects are equals or not.
	 * 
	 * <p>There are different cases for evaluating the equivalence of 2 
	 * objects:
	 * <ul>
	 *   <li>If both 2 objects are null, the method will return {@code true}; 
	 *   If one is null and another is not null, the method will return 
	 *   {@code false}.
	 *   <li>If both 2 objects are {@code List}, they are equal only if they 
	 *   have the same number of elements and each element in one {@code List} 
	 *   has the same element in another {@code List}.
	 * </ul> 
	 * 
     * Check if two objects are "equivalent", which we assert to mean
     * either both are null, or B is polymorphically similar to A and
     * all values are equivalent.  For primitives == will be applied,
     * for primitive wrappers "equals()" method, otherwise equivalent will
     * be recursively called on all complex data structures.
     *
     * @param  obj1
     *         The first object.
     * 
     * @param  obj2
     *         The second object.
     * 
     * @return  {@code true} if those objects are equal;
     *          {@code false} otherwise;
     */
    public static boolean isEqual(Object obj1, Object obj2) {
    		if (obj1 == null || obj2 == null) {
    			if (obj1 == null && obj2 == null) {
    				return true;
    			} else {
    				return false;
    			}
    		}

        // TODO map, array, iterable
        if(List.class.isAssignableFrom(obj1.getClass()) && List.class.isAssignableFrom(obj2.getClass()) ) {
        		return isEqualList((List<?>) obj1, (List<?>) obj2);
        }

        if(!obj1.getClass().isAssignableFrom(obj2.getClass())) {
            return false;
        }

        if(ReflectUtil.isPrimitive(obj1.getClass())) {
            return obj1.equals(obj2);
        }

        // check if objects are equivalent (recursive)
        try {
            Map<String,Class> propertyMap = getPropertyMap(obj1.getClass());
            for(String prop : propertyMap.keySet()) {

                //Class propClass = propertyMap.get(prop);
                Object valueA = PropertyUtils.getProperty(obj1, prop);
                Object valueB = PropertyUtils.getProperty(obj2, prop);

                boolean equivalent = isEqual(valueA, valueB);
                if(!equivalent) {

                    // if any value is non-equivalent, then none are
                    return false;
                }
            }


        }
        catch(Exception e) {
            // shouldn't happen
            return false;
        }

        return true;
    }
    
    /**
     * Check 2 lists are equal or not.
     * 
     * @param  list1
     *         The first list.
     *         
     * @param  list2
     *         The second list.
     * 
     * @return  {@code true} if 2 lists are equal;
     *          {@code false} otherwise.
     */
    public static boolean isEqualList(List<?> list1, List<?> list2) {
        if(list1.size() != list2.size()) {
            return false;
        }

        Iterator<?> iter1 = list1.iterator();
        Iterator<?> iter2 = list2.iterator();

        while(iter1.hasNext()) {
            Object ele1 = iter1.next();
            Object ele2 = iter2.next();

            if(!isEqual(ele1, ele2)) {
                return false;
            }
        }

        return true;
    }
    
    /**
     * Returns a list of all bean properties, including those inherited from superclass (accessible getter method must be defined).
     *
     * @param c
     * @return
     */
    public static Map<String, Class> getPropertyMap(Class<? extends Object> c) {

        return getPropertyMap(c, false, false);
    }


    /**
     * Returns a list of bean property names (accessible getter method must be defined).
     * If setter flag is specified, then an accessible setter method must be defined).
     *
     * @param c
     * @return
     */
    public static Map<String, Class> getPropertyMap(Class<? extends Object> c, boolean recurse, boolean setter) {

        if(c == null) {
            return null;
        }

        // NOTE: LinkedHashMap used in case ordering matters to caller
        Map<String,Class> map = new LinkedHashMap<>();


        // get top level fields (will recurse later as needed)
        Map<String, Class<?>> fieldMap = ReflectUtil.getFieldMap(c, false);

        for(Map.Entry<String,Class<?>> entry : fieldMap.entrySet()) {

            String prop = entry.getKey();
            Class<? extends Object> propClass = entry.getValue();

            // determine if getter/setter method exists and is accessible, otherwise ignore
            try {

                Method method = getBeanMethod(c, prop, propClass, setter);

                if(recurse && propClass.isPrimitive() == false && ReflectUtil.isPrimitiveWrapper(propClass) == false) {

                    Map<String, Class> childMap = getPropertyMap(propClass, true, setter);

                    for(String childField : childMap.keySet()) {

                        Class<? extends Object> childClass = childMap.get(childField);

                        // define children as nested property
                        String nested = prop + "." + childField;
                        map.put(nested, childClass);
                    }
                }
                else {

                    map.put(prop, propClass);
                }

                // TODO - handle case where class is abstract or interface
            }
            catch(NoSuchMethodException e) {
                // ok if not found, we are testing for existence of getter/setter
            }

        }

        return map;
    }
    
    /**
     * @param c
     * @param propertyName
     * @param propertyClass
     * @param setter
     * @return
     * @throws NoSuchMethodException
     */
    public static Method getBeanMethod(Class<? extends Object> c, String propertyName, Class<? extends Object> propertyClass, boolean setter) throws NoSuchMethodException {

        String beanPrefix = (setter) ? "set" : "get";

        // build method name from type and field name
        String methodName = beanPrefix + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1, propertyName.length());

        Class[] args = (setter) ? new Class[]{propertyClass} : null;

        try {
            return c.getMethod(methodName, args);
        }
        catch(NoSuchMethodException e) {

            // handle isBoolean() vs getBoolean()
            if(!setter) {
                if(Boolean.class.isAssignableFrom(propertyClass) || boolean.class.isAssignableFrom(propertyClass)) {
                    methodName = "is" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1, propertyName.length());
                    return c.getMethod(methodName);
                }
            }

            throw e;
        }

    }
}
