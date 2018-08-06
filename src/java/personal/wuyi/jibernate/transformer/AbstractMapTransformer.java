package personal.wuyi.jibernate.transformer;

import personal.wuyi.reflect.ReflectUtil;

import org.apache.commons.beanutils.PropertyUtils;

import java.util.List;
import java.util.Map;

/**
 * Abstract map transformer
 * 
 * <p>Transforms one object into another via a mapping of source object 
 * properties to transformed object properties.
 * 
 * @author  Wuyi Chen
 */
public abstract class AbstractMapTransformer extends AbstractTransformer<Object, Object> {
	protected Class<?>           targetClass;
	protected Map<String,String> map;
	
	@Override
	public Object transform(Object source, Object... context) {
		// do not transform primitive
		if(source == null || ReflectUtil.isPrimitiveWrapper(source.getClass())) {
			return source;
		}

		Object transformed = super.transform(source, context);

		try {
			int index = 0;
			for (String prop : map.keySet()) {
				Object value = null;

				if (source != null && source instanceof List) {
					value = getValue(source, index);
				} else {
					value = getValue(source, prop);
				}

				String mappedProp = map.get(prop);
				value = transform(source, value, mappedProp, targetClass);
				setValue(transformed, mappedProp, value);
				index++;
			}
			return transformed;
		} catch(Exception e) {
			
		}
		
		return null;
	}
	
	/**
	 * Transform a source value in context of target property
	 * 
	 * @param source
	 * @param value
	 * @param property
	 * @param target
	 * @return
	 */
	protected Object transform(Object source, Object value, String property, Class<?> target) {
		return value;
	}
	
	/**
	 * Get mapped property from source.
	 * 
	 * @param source
	 * @param property
	 * @return
	 */
	protected Object getValue(Object source, String property) {
		// do not transform primitive
		if(source == null || ReflectUtil.isPrimitiveWrapper(source.getClass())) {
			return source;
		}
		
        try {
            return PropertyUtils.getProperty(source, property);
        } catch(Exception e) {
            
        }
        return null;
	}
	
	/**
	 * If source is a list, then coerce index to behave as a property.
	 * 
	 * @param source
	 * @param index
	 * @return
	 */
	protected Object getValue(Object source, int index) {
		return getValue(source, "[" + index + "]");
	}
	
	/**
	 * Set mapped property on target.
	 * 
	 * This is the place to do any property specific value transformation if 
	 * required, otherwise value is just passed through by default.
	 * 
	 * @param target
	 * @param property
	 * @param value
	 * @return
	 */
	protected void setValue(Object target, String property, Object value) {
        try {
            PropertyUtils.setProperty(target, property, value);
        } catch(Exception e) {
           
        }
	}
}
