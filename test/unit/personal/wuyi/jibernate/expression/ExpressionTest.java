package personal.wuyi.jibernate.expression;

import org.junit.Assert;
import org.junit.Test;

public class ExpressionTest {
	Expression sinExpr       = new Expression("firstName", Expression.EQUAL, "John");
	Expression com2AndExpr   = new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23);
	Expression com2OrExpr    = new Expression("firstName", Expression.EQUAL, "John").or("firstName", Expression.EQUAL, "Johnson");
	Expression com3AndExpr   = new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23).and("score", Expression.EQUAL, 99);
	Expression com3OrExpr    = new Expression("firstName", Expression.EQUAL, "John").or("firstName", Expression.EQUAL, "Johnson").or("firstName", Expression.EQUAL, "Johnny");
	Expression com3AndOrExpr = new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23).or("firstName", Expression.EQUAL, "Johnny");
	
	@Test
	public void isCompoundTest() {		
		Assert.assertFalse(sinExpr.isCompound());
		Assert.assertTrue(com2AndExpr.isCompound());
		Assert.assertTrue(com2OrExpr.isCompound());
		Assert.assertTrue(com3AndExpr.isCompound());
		Assert.assertTrue(com3OrExpr.isCompound());
	}
	
	@Test
	public void getNumberOfSubExpressionTest() {
		Assert.assertEquals(0, sinExpr.getNumberOfSubExpression());
		Assert.assertEquals(2, com2AndExpr.getNumberOfSubExpression());
		Assert.assertEquals(2, com2OrExpr.getNumberOfSubExpression());
		Assert.assertEquals(3, com3AndExpr.getNumberOfSubExpression());
		Assert.assertEquals(3, com3OrExpr.getNumberOfSubExpression());
	}
	
	@Test
	public void andTest() {
		Assert.assertEquals(Expression.AND, com2AndExpr.getOperator(1));
		Assert.assertEquals(Expression.AND, com3AndExpr.getOperator(1));
		Assert.assertEquals(Expression.AND, com3AndExpr.getOperator(2));
	}
	
	@Test
	public void orTest() {
		Assert.assertEquals(Expression.OR,  com2OrExpr.getOperator(1));
		Assert.assertEquals(Expression.OR,  com3OrExpr.getOperator(1));
		Assert.assertEquals(Expression.OR,  com3OrExpr.getOperator(2));
	}
	
	@Test
	public void getSubExpressionTest() {
		Assert.assertNull(sinExpr.getSubExpression(0));
		Assert.assertEquals(new Expression("age", Expression.EQUAL, 23), com2AndExpr.getSubExpression(1));
		Assert.assertEquals(new Expression("age", Expression.EQUAL, 23), com3AndExpr.getSubExpression(1));
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void getSubExpressionTestException() {
		com3AndExpr.getSubExpression(20);
	}
	
	@Test
	public void replaceSubExpressionTest() {
		sinExpr.replaceSubExpression(1, new Expression("age", Expression.EQUAL, 23));
		
		// update existing sub-expression
		com2AndExpr.replaceSubExpression(1, new Expression("lastName", Expression.STARTS_WITH, "Jr"));
		Assert.assertEquals(2, com2AndExpr.getNumberOfSubExpression());                                                  // size should not change
		Assert.assertEquals(new Expression("lastName", Expression.STARTS_WITH, "Jr"), com2AndExpr.getSubExpression(1));  // check new sub-expression has been updated or not
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void replaceSubExpressionTestException() {
		// add new sub-expression at the end
		com3AndExpr.replaceSubExpression(3, new Expression("lastName", Expression.STARTS_WITH, "Jr"));
		Assert.assertEquals(4, com3AndExpr.getNumberOfSubExpression());                                                  // size should be increased 1
		Assert.assertEquals(new Expression("lastName", Expression.STARTS_WITH, "Jr"), com3AndExpr.getSubExpression(3));  // check new sub-expression has been updated or not
	}
	
	@Test
	public void getOperatorTest() {
		Assert.assertNull(sinExpr.getOperator(0));
		Assert.assertNull(com2AndExpr.getOperator(0));    // no operator on the left side of the first sub-expression
		Assert.assertEquals(Expression.AND, com2AndExpr.getOperator(1));
		Assert.assertEquals(Expression.AND, com3AndExpr.getOperator(1));
		Assert.assertEquals(Expression.AND, com3AndExpr.getOperator(2));
		Assert.assertEquals(Expression.OR,  com2OrExpr.getOperator(1));
		Assert.assertEquals(Expression.OR,  com3OrExpr.getOperator(1));
		Assert.assertEquals(Expression.OR,  com3OrExpr.getOperator(2));
		Assert.assertEquals(Expression.AND, com3AndOrExpr.getOperator(1));
		Assert.assertEquals(Expression.OR,  com3AndOrExpr.getOperator(2));
		
		Assert.assertNull(sinExpr.getOperator(0, Expression.SIDE_LEFT));
		Assert.assertNull(sinExpr.getOperator(0, Expression.SIDE_RIGHT));
	}
}
