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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@code ExpressionEngine}.
 * 
 * @author  Wuyi Chen
 * @date    09/18/2018
 * @version 1.1
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
	
	/**
	 * test evaluate an expression
	 */
	@Test
	public void evaluateTest1() {
		// test subject.value = null
		Assert.assertFalse(ExpressionEngine.evaluate(new Expression("firstName", Expression.EQUAL, "John")));    // subject.name = firstName, subject.value = null
		
		// test simple expression
		Assert.assertTrue(ExpressionEngine.evaluate(new Expression(new Subject("firstName", "John"), Expression.EQUAL, "John")));
		
		// test compound expression
		Assert.assertTrue(ExpressionEngine.evaluate(new Expression(new Subject("firstName", "John"), Expression.EQUAL, "John").or(new Expression(new Subject("lastName", "Andy"), Expression.EQUAL, "Andy"))));
		Assert.assertTrue(ExpressionEngine.evaluate(new Expression(new Subject("firstName", "John"), Expression.EQUAL, "John").and(new Expression(new Subject("lastName", "Andy"), Expression.EQUAL, "Andy"))));
	}
	
	/**
	 * test evaluate an combination of a subject, an operator and a value
	 */
	@Test
	public void evaluateTest2() {
		// test some special cases
		Assert.assertTrue(ExpressionEngine.evaluate(null, Expression.EQUAL, null));
		Assert.assertFalse(ExpressionEngine.evaluate(null, Expression.NOT_EQUAL, null));
		Assert.assertTrue(ExpressionEngine.evaluate(123L, Expression.NOT_EQUAL, null));
		Assert.assertTrue(ExpressionEngine.evaluate(null, Expression.NOT_EQUAL, 123L));
		
		// test equal
		Assert.assertTrue(ExpressionEngine.evaluate(123L, Expression.EQUAL, 123L));
		Assert.assertFalse(ExpressionEngine.evaluate(123L, Expression.EQUAL, 234L));
		
		// test not equal
		Assert.assertTrue(ExpressionEngine.evaluate(123L, Expression.NOT_EQUAL, 234L));
		Assert.assertFalse(ExpressionEngine.evaluate(123L, Expression.NOT_EQUAL, 123L));
		
		// test greater than
		Assert.assertTrue(ExpressionEngine.evaluate(234L, Expression.GREATER_THAN, 123L));
		Assert.assertFalse(ExpressionEngine.evaluate(123L, Expression.GREATER_THAN, 123L));
		Assert.assertFalse(ExpressionEngine.evaluate(123L, Expression.GREATER_THAN, 234L));
		
		// test greater than equal
		Assert.assertTrue(ExpressionEngine.evaluate(234L, Expression.GREATER_THAN_EQUAL, 123L));
		Assert.assertTrue(ExpressionEngine.evaluate(123L, Expression.GREATER_THAN_EQUAL, 123L));
		Assert.assertFalse(ExpressionEngine.evaluate(123L, Expression.GREATER_THAN_EQUAL, 234L));
		
		// test less than
		Assert.assertTrue(ExpressionEngine.evaluate(123L, Expression.LESS_THAN, 234L));
		Assert.assertFalse(ExpressionEngine.evaluate(123L, Expression.LESS_THAN, 123L));
		Assert.assertFalse(ExpressionEngine.evaluate(234L, Expression.LESS_THAN, 123L));
		
		// test less than equal
		Assert.assertTrue(ExpressionEngine.evaluate(123L, Expression.LESS_THAN_EQUAL, 234L));
		Assert.assertTrue(ExpressionEngine.evaluate(123L, Expression.LESS_THAN_EQUAL, 123L));
		Assert.assertFalse(ExpressionEngine.evaluate(234L, Expression.LESS_THAN_EQUAL, 123L));
		
		// test start with
		Assert.assertTrue(ExpressionEngine.evaluate("abcdefg", Expression.STARTS_WITH, "abc"));
		Assert.assertFalse(ExpressionEngine.evaluate("abcdefg", Expression.STARTS_WITH, "abd"));
		
		// test end with
		Assert.assertTrue(ExpressionEngine.evaluate("abcdefg", Expression.ENDS_WITH,   "efg"));
		Assert.assertFalse(ExpressionEngine.evaluate("abcdefg", Expression.ENDS_WITH,   "dfg"));
	}
	
	@Test
	public void evaluateTestException() {
		// two types are not assignable from each other
		try {
			ExpressionEngine.evaluate("123", Expression.EQUAL, 123L);
			fail("Expected an java.lang.IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("java.lang.String is not comparable to java.lang.Long"));
		}
		
		// 1st or 2nd operand is not comparable
		try {
			ExpressionEngine.evaluate(new StringBuilder(), Expression.EQUAL, new StringBuilder());
			fail("Expected an java.lang.IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("java.lang.StringBuilder is not comparable"));
		}
		
		// invalid operator
		try {
			Assert.assertTrue(ExpressionEngine.evaluate(123L, "XXYYZZ", 123L));
			fail("Expected an java.lang.IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("Comparison XXYYZZ not supported."));
		}
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
	
	@Test
	public void unionTest() {
		Expression exprA = new Expression("A", Expression.EQUAL, "a");
		Expression exprB = new Expression("B", Expression.EQUAL, "b");
		Expression exprAAndB = new Expression("A", Expression.EQUAL, "a").and("B", Expression.EQUAL, "b");
		Expression exprCAndD = new Expression("C", Expression.EQUAL, "c").and("D", Expression.EQUAL, "d");
		
		// union(a, b) = a + b
		Assert.assertEquals(new Expression("A", Expression.EQUAL, "a").or("B", Expression.EQUAL, "b"), ExpressionEngine.union(exprA, exprB));
		
		// union(a, c * d) = a + c * d
		Assert.assertEquals(new Expression("A", Expression.EQUAL, "a")
				.or("C", Expression.EQUAL, "c")
				.and("D", Expression.EQUAL, "d"), ExpressionEngine.union(exprA, exprCAndD));
		
		// union(c * d, a) = c * d + a
		Assert.assertEquals(new Expression("C", Expression.EQUAL, "c")
				.and("D", Expression.EQUAL, "d")
				.or("A", Expression.EQUAL, "a"), ExpressionEngine.union(exprCAndD, exprA));
		
		// union(c * d, a) = c * d + a
		Assert.assertEquals(new Expression("A", Expression.EQUAL, "a")
				.and("B", Expression.EQUAL, "b")
				.or("C", Expression.EQUAL, "c")
				.and("D", Expression.EQUAL, "d"), ExpressionEngine.union(exprAAndB, exprCAndD));
	}
	
	@Test
	public void intersectionTest() {
		Expression exprA = new Expression("A", Expression.EQUAL, "a");
		Expression exprB = new Expression("B", Expression.EQUAL, "b");
		Expression exprAOrB = new Expression("A", Expression.EQUAL, "a").or("B", Expression.EQUAL, "b");
		Expression exprCOrD = new Expression("C", Expression.EQUAL, "c").or("D", Expression.EQUAL, "d");
		
		// intersect(a, b) = a * b
		Assert.assertEquals(new Expression("A", Expression.EQUAL, "a").and("B", Expression.EQUAL, "b"), ExpressionEngine.intersection(exprA, exprB));
		
		// intersect(a, c + d) = a * c + a * d
		Assert.assertEquals(new Expression("A", Expression.EQUAL, "a")
				.and("C", Expression.EQUAL, "c")
				.or("A", Expression.EQUAL, "a")
				.and("D", Expression.EQUAL, "d"), ExpressionEngine.intersection(exprA, exprCOrD));
		
		// intersect(c + d, a) = c * a + c * d
		Assert.assertEquals(new Expression("A", Expression.EQUAL, "a")
				.and("C", Expression.EQUAL, "c")
				.or("A", Expression.EQUAL, "a")
				.and("D", Expression.EQUAL, "d"), ExpressionEngine.intersection(exprCOrD, exprA));
		
		// intersect(a + b, c + d) = a * c + a * d + b * c + b * d
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
	public void parseSimpleExpressionTest() throws ParseException {
		// parse invalid string
		Assert.assertEquals(null,              ExpressionEngine.parse(null));
		Assert.assertEquals(null,              ExpressionEngine.parse(""));
		Assert.assertEquals(null,              ExpressionEngine.parse("     "));
		Assert.assertEquals(null,              ExpressionEngine.parse("abc"));
		
		// parse value is string
		Assert.assertEquals(sinExpr,           ExpressionEngine.parse("([firstName]==\"John\")"));
		
		// parse value is list of string
		List<String> valueList1 = Arrays.asList("John", "Johnny", "Johnson");
		Assert.assertEquals(new Expression("firstName", Expression.EQUAL, valueList1), ExpressionEngine.parse("([firstName]==[\"John\",\"Johnny\",\"Johnson\"])"));
		
		// parse value is list of integer
		List<Integer> valueList2 = Arrays.asList(11, 22, 33);
		Assert.assertEquals(new Expression("firstName", Expression.EQUAL, valueList2), ExpressionEngine.parse("([firstName]==[11,22,33])"));
		
		// parse value is null
		Assert.assertEquals(new Expression("firstName", Expression.EQUAL, null),       ExpressionEngine.parse("([firstName]==null)"));
		
		// parse value is boolean
		Assert.assertEquals(new Expression("firstName", Expression.EQUAL, true),       ExpressionEngine.parse("([firstName]==true)"));
		
		// parse value is date
		Assert.assertEquals(new Expression("firstName", Expression.EQUAL, new SimpleDateFormat("MM/dd/yyyy").parse("11/29/2018")), ExpressionEngine.parse("([firstName]==11/29/2018)"));
		
		// parse vavlue is double
		Assert.assertEquals(new Expression("firstName", Expression.EQUAL, 2.3), ExpressionEngine.parse("([firstName]==2.3)"));
	}
	
	@Test
	public void parseCompoundExpression() {
		// parse compound expression
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
