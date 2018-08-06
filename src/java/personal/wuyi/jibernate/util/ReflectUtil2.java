package personal.wuyi.jibernate.util;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import personal.wuyi.reflect.ReflectUtil;

public class ReflectUtil2 {
	/**
     * Check if two objects are "equivalent", which we assert to mean
     * either both are null, or B is polymorphically similar to A and
     * all values are equivalent.  For primitives == will be applied,
     * for primitive wrappers "equals()" method, otherwise equivalent will
     * be recursively called on all complex data structures.
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean equivalent( Object a, Object b ) {

        if( a == null ) {
            if( b == null ) {
                return true;
            }
            return false;
        }
        else if( b == null ) {
            return false;
        }

        // TODO map, array, iterable
        if( List.class.isAssignableFrom( a.getClass() ) && List.class.isAssignableFrom( b.getClass() )  ) {

            List m = (List) a;
            List n = (List) b;

            if( m.size() != n.size() ) {
                return false;
            }

            Iterator i = m.iterator();
            Iterator j = n.iterator();

            while( i.hasNext() ) {

                Object aa = i.next();
                Object bb = j.next();

                if( equivalent( aa, bb ) == false ) {
                    return false;
                }
            }

            return true;
        }

        if( a.getClass().isAssignableFrom( b.getClass() ) == false ) {

            return false;
        }

        // do primitive (wrapper) comparison
        if( ReflectUtil.isPrimitive( a.getClass() ) ) {
            return a.equals( b );
        }



        // check if objects are equivalent (recursive)
        try {
            Map<String,Class> propertyMap = getPropertyMap( a.getClass() );
            for( String prop : propertyMap.keySet() ) {

                //Class propClass = propertyMap.get( prop );
                Object valueA = PropertyUtils.getProperty( a, prop );
                Object valueB = PropertyUtils.getProperty( b, prop );

                boolean equivalent = equivalent( valueA, valueB );
                if( !equivalent ) {

                    // if any value is non-equivalent, then none are
                    return false;
                }
            }


        }
        catch( Exception e ) {
            // shouldn't happen
            return false;
        }

        return true;
    }
    
    /**
     * Returns a list of all bean properties, including those inherited from superclass (accessible getter method must be defined).
     *
     * @param c
     * @return
     */
    public static Map<String, Class> getPropertyMap( Class c ) {

        return getPropertyMap( c, false, false );
    }


    /**
     * Returns a list of bean property names (accessible getter method must be defined).
     * If setter flag is specified, then an accessible setter method must be defined).
     *
     * @param c
     * @return
     */
    public static Map<String, Class> getPropertyMap( Class c, boolean recurse, boolean setter ) {

        if( c == null ) {
            return null;
        }

        // NOTE: LinkedHashMap used in case ordering matters to caller
        Map<String,Class> map = new LinkedHashMap<>();


        // get top level fields (will recurse later as needed)
        Map<String, Class<?>> fieldMap = ReflectUtil.getFieldMap( c, false );

        for( Map.Entry<String,Class<?>> entry : fieldMap.entrySet() ) {

            String prop = entry.getKey();
            Class propClass = entry.getValue();

            // determine if getter/setter method exists and is accessible, otherwise ignore
            try {

                Method method = getBeanMethod( c, prop, propClass, setter );

                if( recurse && propClass.isPrimitive() == false && ReflectUtil.isPrimitiveWrapper( propClass ) == false ) {

                    Map<String, Class> childMap = getPropertyMap( propClass, true, setter );

                    for( String childField : childMap.keySet() ) {

                        Class childClass = childMap.get( childField );

                        // define children as nested property
                        String nested = prop + "." + childField;
                        map.put( nested, childClass );
                    }
                }
                else {

                    map.put( prop, propClass );
                }

                // TODO - handle case where class is abstract or interface
            }
            catch( NoSuchMethodException e ) {
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
    public static Method getBeanMethod( Class c, String propertyName, Class propertyClass, boolean setter ) throws NoSuchMethodException {

        String beanPrefix = (setter) ? "set" : "get";

        // build method name from type and field name
        String methodName = beanPrefix + propertyName.substring( 0, 1 ).toUpperCase() + propertyName.substring( 1, propertyName.length() );

        Class[] args = (setter) ? new Class[]{propertyClass} : null;

        try {
            return c.getMethod( methodName, args );
        }
        catch( NoSuchMethodException e ) {

            // handle isBoolean() vs getBoolean()
            if( !setter ) {
                if( Boolean.class.isAssignableFrom( propertyClass ) || boolean.class.isAssignableFrom( propertyClass ) ) {
                    methodName = "is" + propertyName.substring( 0, 1 ).toUpperCase() + propertyName.substring( 1, propertyName.length() );
                    return c.getMethod( methodName );
                }
            }

            throw e;
        }

    }
}
