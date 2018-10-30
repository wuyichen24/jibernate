/*
 * Copyright 2018 Wuyi Chen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package personal.wuyi.jibernate.expression;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@code ExpressionEngine}.
 * 
 * @author  Wuyi Chen
 * @date    09/18/2018
 * @version 1.0
 * @since   1.0
 */
public class ExpressionEngineTest {
	private Expression sinExpr           = new Expression("firstName", Expression.EQUAL, "John");
	private Expression com2AndExpr       = new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23);
	private Expression com2OrExpr        = new Expression("firstName", Expression.EQUAL, "John").or("firstName", Expression.EQUAL, "Johnson");
	private Expression com3AndExpr       = new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23).and("score", Expression.EQUAL, 99);
	private Expression com3OrExpr        = new Expression("firstName", Expression.EQUAL, "John").or("firstName", Expression.EQUAL, "Johnson").or("firstName", Expression.EQUAL, "Johnny");
	private Expression com3AndOrExpr     = new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23).or("firstName", Expression.EQUAL, "Johnny");
	private Expression comMultiLevelExpr = new Expression(new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23))
										.or(new Expression("firstName", Expression.EQUAL, "Mary").and("age", Expression.EQUAL, 24).and("score", Expression.EQUAL, 99))
										.or(new Expression("firstName", Expression.EQUAL, "Tony").and("age", Expression.EQUAL, 25).and("lastName", Expression.EQUAL, "Lee"));
	
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
	
	public void unionTest() {
		
	}
	
	@Test
	public void intersectionTest() {
		Expression exprA = new Expression("A", Expression.EQUAL, "a");
		Expression exprB = new Expression("B", Expression.EQUAL, "b");
		Expression exprAOrB = new Expression("A", Expression.EQUAL, "a").or("B", Expression.EQUAL, "b");
		Expression exprCOrD = new Expression("C", Expression.EQUAL, "c").or("D", Expression.EQUAL, "d");
		
		Assert.assertEquals(new Expression("A", Expression.EQUAL, "a").and("B", Expression.EQUAL, "b"), ExpressionEngine.intersection(exprA, exprB));
		Assert.assertEquals(new Expression("A", Expression.EQUAL, "a")
				.and("C", Expression.EQUAL, "c")
				.or("A", Expression.EQUAL, "a")
				.and("D", Expression.EQUAL, "d"), ExpressionEngine.intersection(exprA, exprCOrD));
		Assert.assertEquals(new Expression("A", Expression.EQUAL, "a")
				.and("C", Expression.EQUAL, "c")
				.or("A", Expression.EQUAL, "a")
				.and("D", Expression.EQUAL, "d")
				.or("B", Expression.EQUAL, "b")
				.and("C", Expression.EQUAL, "c")
				.or("B", Expression.EQUAL, "b")
				.and("D", Expression.EQUAL, "d"), ExpressionEngine.intersection(exprAOrB, exprCOrD));
	}
	
	@Test
	public void parseTest() {	
		Assert.assertEquals(null,              ExpressionEngine.parse(null));
		Assert.assertEquals(null,              ExpressionEngine.parse(""));
		Assert.assertEquals(null,              ExpressionEngine.parse("     "));
		Assert.assertEquals(null,              ExpressionEngine.parse("abc"));
		Assert.assertEquals(sinExpr,           ExpressionEngine.parse("([firstName]==\"John\")"));
		Assert.assertEquals(com2AndExpr,       ExpressionEngine.parse("(([firstName]==\"John\") && ([age]==23))"));
		Assert.assertEquals(com2OrExpr,        ExpressionEngine.parse("(([firstName]==\"John\") || ([firstName]==\"Johnson\"))"));
		Assert.assertEquals(com3AndExpr,       ExpressionEngine.parse("(([firstName]==\"John\") && ([age]==23) && ([score]==99))"));
		Assert.assertEquals(com3OrExpr,        ExpressionEngine.parse("(([firstName]==\"John\") || ([firstName]==\"Johnson\") || ([firstName]==\"Johnny\"))"));
		Assert.assertEquals(com3AndOrExpr,     ExpressionEngine.parse("(([firstName]==\"John\") && ([age]==23) || ([firstName]==\"Johnny\"))"));
		Assert.assertEquals(comMultiLevelExpr, ExpressionEngine.parse("((([firstName]==\"John\") && ([age]==23)) || (([firstName]==\"Mary\") && ([age]==24) && ([score]==99)) || (([firstName]==\"Tony\") && ([age]==25) && ([lastName]==\"Lee\")))"));
	}
	
	@Test
	public void isComparisonOperatorTest() {
		Assert.assertFalse(ExpressionEngine.isComparisonOperator('a'));
		Assert.assertFalse(ExpressionEngine.isComparisonOperator(' '));
		Assert.assertTrue(ExpressionEngine.isComparisonOperator('='));
		Assert.assertTrue(ExpressionEngine.isComparisonOperator('>'));
		Assert.assertTrue(ExpressionEngine.isComparisonOperator('<'));
		Assert.assertTrue(ExpressionEngine.isComparisonOperator('!'));
	}
	
	@Test
	public void isLogicalOperatorTest() {
		Assert.assertFalse(ExpressionEngine.isLogicalOperator('a'));
		Assert.assertFalse(ExpressionEngine.isLogicalOperator(' '));
		Assert.assertTrue(ExpressionEngine.isLogicalOperator('&'));
		Assert.assertTrue(ExpressionEngine.isLogicalOperator('|'));
	}
}
