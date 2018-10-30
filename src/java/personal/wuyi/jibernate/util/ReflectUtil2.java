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

package personal.wuyi.jibernate.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	private ReflectUtil2() {}
	
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
    		return obj1 == null && obj2 == null;
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
            		Object value1 = null;
            		Object value2 = null;
            	    try {
            	    	value1 = PropertyUtils.getProperty(obj1, prop);
            	    	value2 = PropertyUtils.getProperty(obj2, prop);
            	    } catch(NoSuchMethodException e) {
            	    	    // If the accessor method of a certain field is not define, the field will be ignored.
                    continue;
                }

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
     * Returns a property map of a class.
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
     * Returns a property map of a class.
     * 
     * <p>The entries in the map reflect all the data members in the class. 
     * If any field is not primitive type or not primitive wrapper class. This 
     * method can give an option for getting the fields from the class of the 
     * field in the original class.
     * 
     * <p>If a field is an enumeration type, this method will grab all the 
     * values from the enumeration class.
     *
     * @param  clazz
     *         The class needs to get the property map.
     * 
     * @param  recurse
     *         The option for checking the fields in the sub-class.
     * 
     * @param  setter
     *         The setter option for checking the field must have setter class.
     * 
     * @return  The property map of a class.
     * 
     * @since   1.0
     */
    public static Map<String, Class<?>> getPropertyMap(Class<? extends Object> clazz, boolean recurse, boolean setter) {
        if(clazz == null) {
            return null;
        }

        Map<String, Class<?>> map      = new LinkedHashMap<>();
        Map<String, Class<?>> fieldMap = ReflectUtil.getFieldMap(clazz, false);

        for(Entry<String,Class<?>> entry : fieldMap.entrySet()) {
            String                  fieldName  = entry.getKey();
            Class<? extends Object> fieldClass = entry.getValue();

            if(recurse && !fieldClass.isPrimitive() && !ReflectUtil.isPrimitiveWrapper(fieldClass) && !fieldClass.isEnum()) {
            	Map<String, Class<?>> childMap = getPropertyMap(fieldClass, true, setter);

                for(Entry<String, Class<?>> entry1 : childMap.entrySet()) {
                	String nested = fieldName + "." + entry1.getKey();
                	map.put(nested, entry1.getValue());
                }
            } else {
            		map.put(fieldName, fieldClass);
            		
            		if (fieldClass.isEnum() && recurse) {
            			Object[] values = fieldClass.getEnumConstants();
            			for (Object value : values) {
            				map.put(value.toString(), Enum.class);
            			}
            		} 
            }
        }

        return map;
    }
}
