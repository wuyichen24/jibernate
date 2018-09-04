package personal.wuyi.jibernate.expression;

import org.junit.Assert;
import org.junit.Test;

public class ExpressionEngineTest {
	Expression sinExpr           = new Expression("firstName", Expression.EQUAL, "John");
	Expression com2AndExpr       = new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23);
	Expression com2OrExpr        = new Expression("firstName", Expression.EQUAL, "John").or("firstName", Expression.EQUAL, "Johnson");
	Expression com3AndExpr       = new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23).and("score", Expression.EQUAL, 99);
	Expression com3OrExpr        = new Expression("firstName", Expression.EQUAL, "John").or("firstName", Expression.EQUAL, "Johnson").or("firstName", Expression.EQUAL, "Johnny");
	Expression com3AndOrExpr     = new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23).or("firstName", Expression.EQUAL, "Johnny");
	Expression comMultiLevelExpr = new Expression(new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23))
										.or(new Expression("firstName", Expression.EQUAL, "Mary").and("age", Expression.EQUAL, 24).and("score", Expression.EQUAL, 99))
										.or(new Expression("firstName", Expression.EQUAL, "Tony").and("age", Expression.EQUAL, 25).and("lastName", Expression.EQUAL, "Lee"));
	Expression comSop1 = new Expression(new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23))
										.or(new Expression("firstName", Expression.EQUAL, "Mary").and("age", Expression.EQUAL, 24));
	
	@Test
	public void evaluateTest1() {
		Assert.assertFalse(ExpressionEngine.evaluate(new Expression("firstName", Expression.EQUAL, "John")));    // subject.name = firstName, subject.value = null
		Assert.assertTrue(ExpressionEngine.evaluate(new Expression(new Subject("firstName", "John"), Expression.EQUAL, "John")));
	}
	
	@Test
	public void evaluateTest2() {
		Assert.assertTrue(ExpressionEngine.evaluate(null, Expression.EQUAL, null));
		Assert.assertFalse(ExpressionEngine.evaluate(null, Expression.NOT_EQUAL, null));
		Assert.assertTrue(ExpressionEngine.evaluate(123L, Expression.NOT_EQUAL, null));
		Assert.assertTrue(ExpressionEngine.evaluate(null, Expression.NOT_EQUAL, 123L));
		
		Assert.assertTrue(ExpressionEngine.evaluate("abcdefg", Expression.STARTS_WITH, "abc"));
		Assert.assertTrue(ExpressionEngine.evaluate("abcdefg", Expression.ENDS_WITH,   "efg"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void evaluateTestException() {
		ExpressionEngine.evaluate("123", Expression.EQUAL, 123L);
		ExpressionEngine.evaluate('a', Expression.EQUAL, "a");
	}
	
	@Test
	public void getSumOfProductsTest() {
		Assert.assertEquals(sinExpr, ExpressionEngine.getSumOfProducts(sinExpr, 1000));
	}
	
	@Test
	public void getSumOfProductsByDivideAndConquorTest() {
		System.out.println(comMultiLevelExpr.toString());
		System.out.println(ExpressionEngine.getSumOfProductsByDivideAndConquor(comMultiLevelExpr, 3));
	}
	
	@Test
	public void getSumOfProductsByStackTest() {
		// simple expression is not valid for getSumOfProductsByStack().
		// you should call getSumOfProducts() so that it will be returned directly.
		Assert.assertEquals(com2AndExpr, ExpressionEngine.getSumOfProductsByStack(com2AndExpr));
		Assert.assertEquals(com2OrExpr, ExpressionEngine.getSumOfProductsByStack(com2OrExpr));
		
		// result of by stack should be equal to result of by divide & conquor
		System.out.println("" + comSop1);
		System.out.println("By Stack:          " + ExpressionEngine.getSumOfProductsByStack(comSop1));
		System.out.println("By Divide&Conquor: " + ExpressionEngine.getSumOfProductsByDivideAndConquor(comSop1, 3));
	}
	
	@Test
	public void simplifyNestedExpressionTest() {
		// test 2-level nested ((E)) => E
		Expression expr = new Expression();
		expr.combineCompoundExpression(null, sinExpr);
		Assert.assertEquals(1, expr.getNumberOfSubExpression());
		Assert.assertFalse(expr.equals(sinExpr));
		System.out.println("Before simplify: " + expr.toString());
		Expression newExpr = ExpressionEngine.simplifyNestedExpression(expr);
		System.out.println("After simplify:  " + newExpr.toString());
		Assert.assertTrue(newExpr.equals(sinExpr));
		System.out.println();
		
		// test 2-level nested (!(!E)) => E
		Expression expr2 = new Expression();
		expr2.complement();
		Expression exprX = new Expression("firstName", Expression.EQUAL, "John");
		exprX.complement();
		expr2.combineCompoundExpression(null, exprX);
		Assert.assertEquals(1, expr2.getNumberOfSubExpression());
		System.out.println("Before simplify: " + expr2.toString());
		Expression newExpr2 = ExpressionEngine.simplifyNestedExpression(expr2);
		System.out.println("After simplify:  " + newExpr2.toString());
		System.out.println();
		
		// test multi-level nested
		Expression exprA = new Expression();
		exprA.combineCompoundExpression(null, sinExpr);
		Expression exprB = new Expression();
		exprB.combineExpression(null, exprA);
		Expression exprC = new Expression();
		exprC.combineExpression(null, exprB);
		Assert.assertEquals(1, expr.getNumberOfSubExpression());
		Assert.assertFalse(expr.equals(sinExpr));
		System.out.println("Before simplify: " + exprC.toString());
		Expression newExpr3 = ExpressionEngine.simplifyNestedExpression(expr);
		System.out.println("After simplify:  " + newExpr3.toString());
		Assert.assertTrue(newExpr3.equals(sinExpr));
	}
}
