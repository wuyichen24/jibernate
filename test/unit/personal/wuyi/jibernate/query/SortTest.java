package personal.wuyi.jibernate.query;

import org.junit.Assert;
import org.junit.Test;

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
		
	}
	
	@Test
	public void toStringTest() {
		
	}
}
