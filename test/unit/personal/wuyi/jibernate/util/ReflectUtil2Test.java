package personal.wuyi.jibernate.util;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import personal.wuyi.jibernate.entity.Ethnicity;
import personal.wuyi.jibernate.entity.Student;

import org.hamcrest.collection.IsMapContaining;
import static org.hamcrest.MatcherAssert.assertThat;

public class ReflectUtil2Test {
	public void isEqualTest() {
		
	}
	
	public void isEqualListTest() {
		
	}
	
	@Test
	public void getPropertyMapTest() {
		Map<String, Class<?>> map1 = ReflectUtil2.getPropertyMap(Student.class);
		assertThat(map1, IsMapContaining.hasEntry("serialVersionUID", Long.TYPE));
		assertThat(map1, IsMapContaining.hasEntry("id",               Long.class));
		assertThat(map1, IsMapContaining.hasEntry("firstName",        String.class));
		assertThat(map1, IsMapContaining.hasEntry("lastName",         String.class));
		assertThat(map1, IsMapContaining.hasEntry("dob",              Date.class));
		assertThat(map1, IsMapContaining.hasEntry("gpa",              Double.TYPE));
		assertThat(map1, IsMapContaining.hasEntry("race",             Ethnicity.class));
		
		Map<String, Class<?>> map2 = ReflectUtil2.getPropertyMap(Student.class, true, false);
		assertThat(map2, IsMapContaining.hasEntry("serialVersionUID", Long.TYPE));
		assertThat(map2, IsMapContaining.hasEntry("id",               Long.class));
		assertThat(map2, IsMapContaining.hasEntry("firstName",        String.class));
		assertThat(map2, IsMapContaining.hasEntry("lastName",         String.class));
		assertThat(map2, IsMapContaining.hasEntry("dob",              Date.class));
		assertThat(map2, IsMapContaining.hasEntry("gpa",              Double.TYPE));
		assertThat(map2, IsMapContaining.hasEntry("race",             Ethnicity.class));
		assertThat(map2, IsMapContaining.hasEntry("WHITE",            Enum.class));
		assertThat(map2, IsMapContaining.hasEntry("ASIAN",            Enum.class));
		assertThat(map2, IsMapContaining.hasEntry("AMERICAN_INDIAN",  Enum.class));
		assertThat(map2, IsMapContaining.hasEntry("HISPANIC",         Enum.class));
		assertThat(map2, IsMapContaining.hasEntry("BLACK",            Enum.class));
	}
}
