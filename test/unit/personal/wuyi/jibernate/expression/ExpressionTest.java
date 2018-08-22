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
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void getOperatorTestException() {
		com2AndExpr.getOperator(20);
		com2AndExpr.getOperator(-10);
	}
	
	@Test
	public void getOperatorWithSideTest() {
		Assert.assertNull(sinExpr.getOperator(0, Expression.SIDE_LEFT));
		Assert.assertNull(sinExpr.getOperator(0, Expression.SIDE_RIGHT));
		
		Assert.assertNull(com2AndExpr.getOperator(0, Expression.SIDE_LEFT));
		Assert.assertEquals(Expression.AND, com2AndExpr.getOperator(0, Expression.SIDE_RIGHT));
		Assert.assertEquals(Expression.AND, com2AndExpr.getOperator(1, Expression.SIDE_LEFT));
		Assert.assertNull(com2AndExpr.getOperator(1, Expression.SIDE_RIGHT));
		
		Assert.assertNull(com2OrExpr.getOperator(0, Expression.SIDE_LEFT));
		Assert.assertEquals(Expression.OR, com2OrExpr.getOperator(0, Expression.SIDE_RIGHT));
		Assert.assertEquals(Expression.OR, com2OrExpr.getOperator(1, Expression.SIDE_LEFT));
		Assert.assertNull(com2OrExpr.getOperator(1, Expression.SIDE_RIGHT));
		
		Assert.assertNull(com3AndExpr.getOperator(0, Expression.SIDE_LEFT));
		Assert.assertEquals(Expression.AND, com3AndExpr.getOperator(0, Expression.SIDE_RIGHT));
		Assert.assertEquals(Expression.AND, com3AndExpr.getOperator(1, Expression.SIDE_LEFT));
		Assert.assertEquals(Expression.AND, com3AndExpr.getOperator(1, Expression.SIDE_RIGHT));
		Assert.assertEquals(Expression.AND, com3AndExpr.getOperator(2, Expression.SIDE_LEFT));
		Assert.assertNull(com3AndExpr.getOperator(2, Expression.SIDE_RIGHT));
		
		Assert.assertNull(com3OrExpr.getOperator(0, Expression.SIDE_LEFT));
		Assert.assertEquals(Expression.OR, com3OrExpr.getOperator(0, Expression.SIDE_RIGHT));
		Assert.assertEquals(Expression.OR, com3OrExpr.getOperator(1, Expression.SIDE_LEFT));
		Assert.assertEquals(Expression.OR, com3OrExpr.getOperator(1, Expression.SIDE_RIGHT));
		Assert.assertEquals(Expression.OR, com3OrExpr.getOperator(2, Expression.SIDE_LEFT));
		Assert.assertNull(com3OrExpr.getOperator(2, Expression.SIDE_RIGHT));
		
		Assert.assertNull(com3AndOrExpr.getOperator(0, Expression.SIDE_LEFT));
		Assert.assertEquals(Expression.AND, com3AndOrExpr.getOperator(0, Expression.SIDE_RIGHT));
		Assert.assertEquals(Expression.AND, com3AndOrExpr.getOperator(1, Expression.SIDE_LEFT));
		Assert.assertEquals(Expression.OR, com3AndOrExpr.getOperator(1, Expression.SIDE_RIGHT));
		Assert.assertEquals(Expression.OR, com3AndOrExpr.getOperator(2, Expression.SIDE_LEFT));
		Assert.assertNull(com3AndOrExpr.getOperator(2, Expression.SIDE_RIGHT));
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void getOperatorWithSideTestException() {
		com2AndExpr.getOperator(30, Expression.SIDE_RIGHT);
		com3AndOrExpr.getOperator(30, Expression.SIDE_RIGHT);
	}
	
	@Test
	public void setOperatorTest() {
		com2AndExpr.setOperator(0, Expression.SIDE_RIGHT, Expression.OR);
		Assert.assertEquals(Expression.OR, com2AndExpr.getOperator(1, Expression.SIDE_LEFT));
		
		com2OrExpr.setOperator(0, Expression.SIDE_RIGHT, Expression.AND);
		Assert.assertEquals(Expression.AND, com2OrExpr.getOperator(1, Expression.SIDE_LEFT));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setOperatorTestException() {
		com2AndExpr.setOperator(0, Expression.SIDE_LEFT, Expression.AND);   // can not set on the left side of the first expression
		com2AndExpr.setOperator(1, Expression.SIDE_RIGHT, Expression.AND);  // can not set on the right side of the last expression
	}
	
	@Test
	public void addSubExpressionWithOperatorTest() {
		com2AndExpr.addSubExpressionWithOperator(new Expression("score", Expression.EQUAL, 98), Expression.OR);
		Assert.assertEquals(3, com2AndExpr.getNumberOfSubExpression());
		Assert.assertEquals(Expression.OR, com2AndExpr.getOperator(1, Expression.SIDE_RIGHT));
		
		// add one the first sub-expression
		com2OrExpr.addSubExpressionWithOperator(0, new Expression("score", Expression.EQUAL, 99), Expression.AND);
		Assert.assertEquals(3, com2OrExpr.getNumberOfSubExpression());
		Assert.assertEquals(Expression.AND, com2OrExpr.getOperator(1, Expression.SIDE_LEFT));
		
		com3OrExpr.addSubExpressionWithOperator(12, new Expression("score", Expression.EQUAL, 99), Expression.AND);
		Assert.assertEquals(4, com3OrExpr.getNumberOfSubExpression());
		Assert.assertEquals(Expression.AND, com3OrExpr.getOperator(3, Expression.SIDE_LEFT));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addSubExpressionWithOperatorTestException() {
		// check if a simple expression to call this method, this method will throw exception
		sinExpr.addSubExpressionWithOperator(new Expression("age", Expression.EQUAL, 23), Expression.AND);
		Assert.assertEquals(1, sinExpr.getNumberOfSubExpression());
	}
	
	@Test
	public void addCompoundExpressionTest() {
		com2AndExpr.addCompoundExpression(com2OrExpr, Expression.AND);
		Assert.assertEquals(4, com2AndExpr.getNumberOfSubExpression());
		Assert.assertEquals(Expression.AND, com2AndExpr.getOperator(2, Expression.SIDE_LEFT));
		Assert.assertEquals(new Expression("firstName", Expression.EQUAL, "John"),    com2AndExpr.getSubExpression(0));
		Assert.assertEquals(new Expression("age", Expression.EQUAL, 23),              com2AndExpr.getSubExpression(1));
		Assert.assertEquals(new Expression("firstName", Expression.EQUAL, "John"),    com2AndExpr.getSubExpression(2));
		Assert.assertEquals(new Expression("firstName", Expression.EQUAL, "Johnson"), com2AndExpr.getSubExpression(3));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addCompoundExpressionTestException() {
		// check if a simple expression to call this method, this method will throw exception
		sinExpr.addCompoundExpression(com2OrExpr, Expression.AND);
		Assert.assertEquals(3, sinExpr.getNumberOfSubExpression());
	}
	
	@Test
	public void removeSubExpressionTest() {
		// remove sub-expression from a simple expression.
		sinExpr.removeSubExpression(0);
		Assert.assertEquals(new Subject("firstName", null), sinExpr.getSubject());
		Assert.assertEquals(Expression.EQUAL, sinExpr.getOperator());
		Assert.assertEquals("John", sinExpr.getValue());
		Assert.assertFalse(sinExpr.isComplement());
		
		// remove a sub-expression from a compound expression which has 2 sub-expressions. (this method needs to simplify this compound expression)
		Assert.assertTrue(com2AndExpr.isCompound());
		com2AndExpr.removeSubExpression(0);           // remove first sub-expression
		Assert.assertFalse(com2AndExpr.isCompound());
		Assert.assertEquals(0, com2AndExpr.getNumberOfSubExpression());
		Assert.assertEquals(new Subject("age", null), com2AndExpr.getSubject());
		Assert.assertEquals(Expression.EQUAL, com2AndExpr.getOperator());
		Assert.assertEquals(23, com2AndExpr.getValue());
		Assert.assertFalse(com2AndExpr.isComplement());
		
		Assert.assertTrue(com2OrExpr.isCompound());
		com2OrExpr.removeSubExpression(1);            // remove second/last sub-expression
		Assert.assertFalse(com2OrExpr.isCompound());
		Assert.assertEquals(0, com2OrExpr.getNumberOfSubExpression());
		Assert.assertEquals(new Subject("firstName", null), com2OrExpr.getSubject());
		Assert.assertEquals(Expression.EQUAL, com2OrExpr.getOperator());
		Assert.assertEquals("John", com2OrExpr.getValue());
		Assert.assertFalse(com2OrExpr.isComplement());
		
		// remove a sub-expression from a compound expression which has more than 2 sub-expressions. (no simplify needed)
		Assert.assertTrue(com3AndOrExpr.isCompound());
		Assert.assertEquals(3, com3AndOrExpr.getNumberOfSubExpression());
		com3AndOrExpr.removeSubExpression(1);
		Assert.assertTrue(com3AndOrExpr.isCompound());
		Assert.assertEquals(2, com3AndOrExpr.getNumberOfSubExpression());
		Assert.assertEquals(new Expression("firstName", Expression.EQUAL, "John"),   com3AndOrExpr.getSubExpression(0));
		Assert.assertEquals(new Expression("firstName", Expression.EQUAL, "Johnny"), com3AndOrExpr.getSubExpression(1));
		Assert.assertEquals(Expression.OR, com3AndOrExpr.getOperator(1, Expression.SIDE_LEFT));
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void removeSubExpressionTestException() {
		com3AndOrExpr.removeSubExpression(200);
	}
}
