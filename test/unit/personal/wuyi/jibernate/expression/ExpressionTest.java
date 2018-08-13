package personal.wuyi.jibernate.expression;

import org.junit.Assert;
import org.junit.Test;

public class ExpressionTest {
	@Test
	public void isCompoundTest() {
		Expression a = new Expression("firstName", Expression.EQUAL, "John");
		Expression b = new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23);
		Expression c = new Expression("firstName", Expression.EQUAL, "John").or("firstName", Expression.EQUAL, "Johnson");
		
		Assert.assertFalse(a.isCompound());
		Assert.assertTrue(b.isCompound());
		Assert.assertTrue(c.isCompound());
	}
}
