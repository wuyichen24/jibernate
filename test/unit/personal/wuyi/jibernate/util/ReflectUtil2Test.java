package personal.wuyi.jibernate.util;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import personal.wuyi.jibernate.entity.Student;

public class ReflectUtil2Test {
	public void isEqualTest() {
		
	}
	
	public void isEqualListTest() {
		
	}
	
	@Test
	public void getPropertyMapTest() {
		Map<String, Class<?>> map = ReflectUtil2.getPropertyMap(Student.class);
		for (Entry<String, Class<?>> entry : map.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
	}
}
