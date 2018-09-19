package personal.wuyi.jibernate.expression;

import org.junit.Test;

import junit.framework.Assert;

/**
 * Test class for {@code Subject}.
 * 
 * @author  Wuyi Chen
 * @date    09/18/2018
 * @version 1.0
 * @since   1.0
 */
public class SubjectTest {
	@Test
	public void cloneTest() {
		Subject oldSubject    = new Subject("first name", "John");
		Subject clonedSubject = (Subject) oldSubject.clone();
		clonedSubject.setName("last name");
		clonedSubject.setValue("Mary");	
		Assert.assertEquals("first name", oldSubject.getName());
		Assert.assertEquals("John",       oldSubject.getValue());
	}
	
	@Test
	public void equalsTest() {
		Subject sub1 = new Subject("first name", "John");
		
		Assert.assertFalse(sub1.equals(null));
		Assert.assertFalse(sub1.equals("first name"));
		
		Subject sub2 = new Subject("first name", "Mary");
		Assert.assertFalse(sub1.equals(sub2));
		
		Subject sub3 = new Subject("last name", "John");
		Assert.assertFalse(sub1.equals(sub3));
		
		Subject sub4 = new Subject("first name", "John");
		Assert.assertTrue(sub1.equals(sub4));
	}
}
