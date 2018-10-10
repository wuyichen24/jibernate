package personal.wuyi.jibernate.query;

import org.junit.Assert;
import org.junit.Test;

/**
 * The test class for {@code Sort}.
 * 
 * @author  Wuyi Chen
 * @date    10/09/2018
 * @version 1.0
 * @since   1.0
 */
public class SortTest {
	@Test
	public void parseTest() {
		Sort sort1 = Sort.parse("firstname+");
		Assert.assertEquals("firstname", sort1.getField());
		Assert.assertTrue(sort1.isAscending());
		
		Sort sort2 = Sort.parse("firstname-");
		Assert.assertEquals("firstname", sort2.getField());
		Assert.assertFalse(sort2.isAscending());
		
		Sort sort3 = Sort.parse("firstname+,lastname-,gpa-");
		Assert.assertEquals("firstname", sort3.toList().get(0).getField());
		Assert.assertEquals("lastname",  sort3.toList().get(1).getField());
		Assert.assertEquals("gpa",       sort3.toList().get(2).getField());
		Assert.assertTrue(sort3.toList().get(0).isAscending());
		Assert.assertFalse(sort3.toList().get(1).isAscending());
		Assert.assertFalse(sort3.toList().get(2).isAscending());
	}
	
	@Test
	public void addTest() {
		Sort sort1 = new Sort("firstname", true);
		sort1.add("lastname", false);
		Assert.assertEquals("firstname", sort1.toList().get(0).getField());
		Assert.assertEquals("lastname",  sort1.toList().get(1).getField());
		Assert.assertTrue(sort1.toList().get(0).isAscending());
		Assert.assertFalse(sort1.toList().get(1).isAscending());
		
		Sort sort2 = new Sort("gpa", true);
		sort2.add("age", false);
		sort1.add(sort2);
		Assert.assertEquals("firstname", sort1.toList().get(0).getField());
		Assert.assertEquals("lastname",  sort1.toList().get(1).getField());
		Assert.assertEquals("gpa",       sort1.toList().get(2).getField());
		Assert.assertEquals("age",       sort1.toList().get(3).getField());
		Assert.assertTrue(sort1.toList().get(0).isAscending());
		Assert.assertFalse(sort1.toList().get(1).isAscending());
		Assert.assertTrue(sort1.toList().get(2).isAscending());
		Assert.assertFalse(sort1.toList().get(3).isAscending());
	}
	
	@Test
	public void toStringTest() {
		Sort sort1 = new Sort("firstname", true).add("lastname", false).add("gpa", true).add("age", false);
		Assert.assertEquals("firstname+,lastname-,gpa+,age-", sort1.toString());
	}
}
