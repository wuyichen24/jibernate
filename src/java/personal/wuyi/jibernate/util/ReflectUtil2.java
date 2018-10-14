package personal.wuyi.jibernate.util;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import personal.wuyi.reflect.ReflectUtil;

/**
 * The tool class for reflection.
 * 
 * @author  Wuyi Chen
 * @date    09/18/2018
 * @version 1.0
 * @since   1.0
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
	 *   <li>If both 2 objects are in primitive types, so they will be 
	 *   evaluated by ==. If 2 objects are primitive wrappers, so 
	 *   {@code equals()} will be applied.
	 *   <li>Other cases, those 2 objects will be evaluated recursively.
	 * </ul> 
     *
     * @param  obj1
     *         The first object.
     * 
     * @param  obj2
     *         The second object.
     * 
     * @return  {@code true} if those objects are equal;
     *          {@code false} otherwise;
     *          
     * @since   1.0
     */
    public static boolean isEqual(Object obj1, Object obj2) {
    		if (obj1 == null || obj2 == null) {
    			if (obj1 == null && obj2 == null) {
    				return true;
    			} else {
    				return false;
    			}
    		}
    	
        if(List.class.isAssignableFrom(obj1.getClass()) && List.class.isAssignableFrom(obj2.getClass())) {
        	return isEqualList((List<?>) obj1, (List<?>) obj2);
        }

        if(!obj1.getClass().isAssignableFrom(obj2.getClass())) {
            return false;
        }

        if(ReflectUtil.isPrimitive(obj1.getClass())) {
            return obj1.equals(obj2);
        }

        try {
            Map<String,Class<?>> propertyMap = getPropertyMap(obj1.getClass());
            for(String prop : propertyMap.keySet()) {
                Object value1 = PropertyUtils.getProperty(obj1, prop);
                Object value2 = PropertyUtils.getProperty(obj2, prop);

                boolean equivalent = isEqual(value1, value2);
                if(!equivalent) {
                    return false;
                }
            }
        } catch(Exception e) {
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
     *          
     * @since   1.0
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
     * Returns a property map of a class
     * 
     * <p>The entries in the map reflect all the data members in the class and 
     * its super class. The key of entries is the name of a data member, and 
     * the value of the entries is the type of a data member. 
     *
     * @param  clazz
     *         The class needs to get the property map.
     * 
     * @return  The property map of a class.
     * 
     * @since   1.0
     */
    public static Map<String, Class<?>> getPropertyMap(Class<? extends Object> clazz) {
        return getPropertyMap(clazz, false, false);
    }


    /**
     * Returns a list of bean property names (accessible getter method must be defined).
     * If setter flag is specified, then an accessible setter method must be defined).
     *
     * @param clazz
     * @param recurse
     * @param setter
     * @return
     */
    public static Map<String, Class<?>> getPropertyMap(Class<? extends Object> clazz, boolean recurse, boolean setter) {

        if(clazz == null) {
            return null;
        }

        // NOTE: LinkedHashMap used in case ordering matters to caller
        Map<String,Class<?>> map = new LinkedHashMap<>();


        // get top level fields (will recurse later as needed)
        Map<String, Class<?>> fieldMap = ReflectUtil.getFieldMap(clazz, false);

        for(Map.Entry<String,Class<?>> entry : fieldMap.entrySet()) {

            String prop = entry.getKey();
            Class<? extends Object> propClass = entry.getValue();

            // determine if getter/setter method exists and is accessible, otherwise ignore
            try {

                Method method = getBeanMethod(clazz, prop, propClass, setter);

                if(recurse && propClass.isPrimitive() == false && ReflectUtil.isPrimitiveWrapper(propClass) == false) {

                    Map<String, Class<?>> childMap = getPropertyMap(propClass, true, setter);

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
