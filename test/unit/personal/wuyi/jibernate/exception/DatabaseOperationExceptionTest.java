package personal.wuyi.jibernate.exception;

import org.junit.Test;
import junit.framework.Assert;

/**
 * Test class for {@code DatabaseOperationException}.
 * 
 * @author  Wuyi Chen
 * @date    10/30/2018
 * @version 1.1
 * @since   1.1
 */
public class DatabaseOperationExceptionTest {	
	@Test
	public void constructorTest() {
		Exception e1 = new DatabaseOperationException("aabbccdd");
		Assert.assertEquals("aabbccdd", e1.getMessage());
		
		Exception e2 = new DatabaseOperationException(new IllegalArgumentException());
		Assert.assertEquals(IllegalArgumentException.class, e2.getCause().getClass());
		
		Exception e3 = new DatabaseOperationException("aabbccdd", new IllegalArgumentException());
		Assert.assertEquals("aabbccdd", e3.getMessage());
		Assert.assertEquals(IllegalArgumentException.class, e3.getCause().getClass());
	}
}
