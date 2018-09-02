package personal.wuyi.jibernate.expression;

import org.junit.Assert;
import org.junit.Test;

public class ExpressionEngineTest {
	Expression sinExpr = new Expression("firstName", Expression.EQUAL, "John");
	
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
