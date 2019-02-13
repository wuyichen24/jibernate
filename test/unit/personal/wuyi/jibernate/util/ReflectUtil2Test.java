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
 * @version 1.1
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
		// same list
		List<String> list1 = Arrays.asList("ABC", "123", "XYZ");
		List<String> list2 = Arrays.asList("ABC", "123", "XYZ");
		Assert.assertTrue(ReflectUtil2.isEqualList(list1, list2));
		
		// different list (same size, different elements)
		List<String> list3 = Arrays.asList("ABC", "123", "XYZ");
		List<String> list4 = Arrays.asList("ABC", "XYZ", "123");
		Assert.assertFalse(ReflectUtil2.isEqualList(list3, list4));
		
		// different list (different size)
		List<String> list5 = Arrays.asList("ABC", "123", "XYZ");
		List<String> list6 = Arrays.asList("ABC", "XYZ");
		Assert.assertFalse(ReflectUtil2.isEqualList(list5, list6));
	}
	
	@Test
	public void getPropertyMapTest() {
		// special case
		Assert.assertNull(ReflectUtil2.getPropertyMap(null));
		
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
		
		Map<String, Class<?>> map3 = ReflectUtil2.getPropertyMap(ClassRoom.class, true, false);
		assertThat(map3, IsMapContaining.hasEntry("studentA.serialVersionUID", Long.TYPE));
		assertThat(map3, IsMapContaining.hasEntry("studentA.id",               Long.class));
		assertThat(map3, IsMapContaining.hasEntry("studentA.firstName",        String.class));
		assertThat(map3, IsMapContaining.hasEntry("studentA.lastName",         String.class));
		assertThat(map3, IsMapContaining.hasEntry("studentA.dob",              Date.class));
		assertThat(map3, IsMapContaining.hasEntry("studentA.gpa",              Double.TYPE));
		assertThat(map3, IsMapContaining.hasEntry("studentA.race",             Ethnicity.class));
		assertThat(map3, IsMapContaining.hasEntry("studentA.WHITE",            Enum.class));
		assertThat(map3, IsMapContaining.hasEntry("studentA.ASIAN",            Enum.class));
		assertThat(map3, IsMapContaining.hasEntry("studentA.AMERICAN_INDIAN",  Enum.class));
		assertThat(map3, IsMapContaining.hasEntry("studentA.HISPANIC",         Enum.class));
		assertThat(map3, IsMapContaining.hasEntry("studentA.BLACK",            Enum.class));
		assertThat(map3, IsMapContaining.hasEntry("studentB.serialVersionUID", Long.TYPE));
		assertThat(map3, IsMapContaining.hasEntry("studentB.id",               Long.class));
		assertThat(map3, IsMapContaining.hasEntry("studentB.firstName",        String.class));
		assertThat(map3, IsMapContaining.hasEntry("studentB.lastName",         String.class));
		assertThat(map3, IsMapContaining.hasEntry("studentB.dob",              Date.class));
		assertThat(map3, IsMapContaining.hasEntry("studentB.gpa",              Double.TYPE));
		assertThat(map3, IsMapContaining.hasEntry("studentB.race",             Ethnicity.class));
		assertThat(map3, IsMapContaining.hasEntry("studentB.WHITE",            Enum.class));
		assertThat(map3, IsMapContaining.hasEntry("studentB.ASIAN",            Enum.class));
		assertThat(map3, IsMapContaining.hasEntry("studentB.AMERICAN_INDIAN",  Enum.class));
		assertThat(map3, IsMapContaining.hasEntry("studentB.HISPANIC",         Enum.class));
		assertThat(map3, IsMapContaining.hasEntry("studentB.BLACK",            Enum.class));
		assertThat(map3, IsMapContaining.hasEntry("studentC.serialVersionUID", Long.TYPE));
		assertThat(map3, IsMapContaining.hasEntry("studentC.id",               Long.class));
		assertThat(map3, IsMapContaining.hasEntry("studentC.firstName",        String.class));
		assertThat(map3, IsMapContaining.hasEntry("studentC.lastName",         String.class));
		assertThat(map3, IsMapContaining.hasEntry("studentC.dob",              Date.class));
		assertThat(map3, IsMapContaining.hasEntry("studentC.gpa",              Double.TYPE));
		assertThat(map3, IsMapContaining.hasEntry("studentC.race",             Ethnicity.class));
		assertThat(map3, IsMapContaining.hasEntry("studentC.WHITE",            Enum.class));
		assertThat(map3, IsMapContaining.hasEntry("studentC.ASIAN",            Enum.class));
		assertThat(map3, IsMapContaining.hasEntry("studentC.AMERICAN_INDIAN",  Enum.class));
		assertThat(map3, IsMapContaining.hasEntry("studentC.HISPANIC",         Enum.class));
		assertThat(map3, IsMapContaining.hasEntry("studentC.BLACK",            Enum.class));
	}
}
