package personal.wuyi.jibernate.util;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import junit.framework.Assert;
import personal.wuyi.jibernate.entity.Ethnicity;
import personal.wuyi.jibernate.entity.Student;

import org.hamcrest.collection.IsMapContaining;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * The test class for {@code ReflectUtil2}.
 * 
 * @author  Wuyi Chen
 * @date    10/15/2018
 * @version 1.0
 * @since   1.0
 */
public class ReflectUtil2Test {
	@Test
	public void isEqualTest() {
		Assert.assertTrue(ReflectUtil2.isEqual(null, null));
		Assert.assertFalse(ReflectUtil2.isEqual(null, new Student()));
		Assert.assertFalse(ReflectUtil2.isEqual(new Student(), null));
		Assert.assertFalse(ReflectUtil2.isEqual(new Student(), "  "));
		Assert.assertTrue(ReflectUtil2.isEqual("abcdefg", "abcdefg"));
		Assert.assertTrue(ReflectUtil2.isEqual(123L, 123L));
		Assert.assertFalse(ReflectUtil2.isEqual("abcdefg", "opqrst"));
		Assert.assertTrue(ReflectUtil2.isEqual(new Student("John", "Clash", 3.45), new Student("John", "Clash", 3.45)));
		Assert.assertFalse(ReflectUtil2.isEqual(new Student("John", "Clash", 3.45), new Student("John", "Clash", 3.49)));
	}
	
	@Test
	public void isEqualListTest() {
		List<String> list1 = Arrays.asList("ABC", "123", "XYZ");
		List<String> list2 = Arrays.asList("ABC", "123", "XYZ");
		Assert.assertTrue(ReflectUtil2.isEqualList(list1, list2));
		
		List<String> list3 = Arrays.asList("ABC", "123", "XYZ");
		List<String> list4 = Arrays.asList("ABC", "XYZ", "123");
		Assert.assertFalse(ReflectUtil2.isEqualList(list3, list4));
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
