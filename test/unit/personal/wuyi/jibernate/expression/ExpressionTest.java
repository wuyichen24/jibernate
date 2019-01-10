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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@code Expression}.
 * 
 * @author  Wuyi Chen
 * @date    09/18/2018
 * @version 1.0
 * @since   1.0
 */
public class ExpressionTest {
	private Expression sinAExpr          = new Expression("firstName", Expression.EQUAL, "John");
	private Expression sinBExpr          = new Expression("age", Expression.EQUAL, 23);
	private Expression sinCExpr          = new Expression("score", Expression.EQUAL, 99);
	private Expression com2AndExpr       = new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23);
	private Expression com2OrExpr        = new Expression("firstName", Expression.EQUAL, "John").or("firstName", Expression.EQUAL, "Johnson");
	private Expression com3AndExpr       = new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23).and("score", Expression.EQUAL, 99);
	private Expression com3OrExpr        = new Expression("firstName", Expression.EQUAL, "John").or("firstName", Expression.EQUAL, "Johnson").or("firstName", Expression.EQUAL, "Johnny");
	private Expression com3AndOrExpr     = new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23).or("firstName", Expression.EQUAL, "Johnny");
	private Expression comMultiLevelExpr = new Expression(new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23))
										.or(new Expression("firstName", Expression.EQUAL, "Mary").and("age", Expression.EQUAL, 24).and("score", Expression.EQUAL, 99))
										.or(new Expression("firstName", Expression.EQUAL, "Tony").and("age", Expression.EQUAL, 25).and("lastName", Expression.EQUAL, "Lee"));
	private Expression nestedExpr        = new Expression(new Expression(new Expression("firstName", Expression.EQUAL, "John")));
	
	@Test 
	public void constructorTest() {
		Expression comMultiLevelExpr = new Expression(new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23));
		comMultiLevelExpr.getOperator();
	}
	
	@Test
	public void isCompoundTest() {		
		Assert.assertFalse(sinAExpr.   isCompound());
		Assert.assertTrue(com2AndExpr.isCompound());
		Assert.assertTrue(com2OrExpr. isCompound());
		Assert.assertTrue(com3AndExpr.isCompound());
		Assert.assertTrue(com3OrExpr. isCompound());
	}
	
	@Test
	public void getNumberOfSubExpressionTest() {
		Assert.assertEquals(0, sinAExpr.getNumberOfSubExpression());
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
	public void andStaticTest() {
		Expression newExpr = Expression.and(sinAExpr, sinBExpr, sinCExpr);
		Assert.assertEquals(sinAExpr, newExpr.getSubExpression(0));
		Assert.assertEquals(sinBExpr, newExpr.getSubExpression(1));
		Assert.assertEquals(sinCExpr, newExpr.getSubExpression(2));
		Assert.assertEquals(Expression.AND,  newExpr.getOperator(1));
		Assert.assertEquals(Expression.AND,  newExpr.getOperator(2));
	}
	
	@Test
	public void orTest() {
		Assert.assertEquals(Expression.OR,  com2OrExpr.getOperator(1));
		Assert.assertEquals(Expression.OR,  com3OrExpr.getOperator(1));
		Assert.assertEquals(Expression.OR,  com3OrExpr.getOperator(2));
	}
	
	@Test
	public void orStaticTest() {
		Expression newExpr = Expression.or(sinAExpr, sinBExpr, sinCExpr);
		Assert.assertEquals(sinAExpr, newExpr.getSubExpression(0));
		Assert.assertEquals(sinBExpr, newExpr.getSubExpression(1));
		Assert.assertEquals(sinCExpr, newExpr.getSubExpression(2));
		Assert.assertEquals(Expression.OR,  newExpr.getOperator(1));
		Assert.assertEquals(Expression.OR,  newExpr.getOperator(2));
	}
	
	@Test
	public void getSubExpressionTest() {
		Assert.assertNull(sinAExpr.getSubExpression(0));
		Assert.assertEquals(new Expression("age", Expression.EQUAL, 23), com2AndExpr.getSubExpression(1));
		Assert.assertEquals(new Expression("age", Expression.EQUAL, 23), com3AndExpr.getSubExpression(1));
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void getSubExpressionTestException() {
		com3AndExpr.getSubExpression(20);
	}
	
	@Test
	public void replaceSubExpressionTest() {
		sinAExpr.replaceSubExpression(1, new Expression("age", Expression.EQUAL, 23));
		
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
		Assert.assertNull(sinAExpr.getOperator(0));
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
	
	@Test
	public void getOperatorTestException() {
		try {
			com2AndExpr.getOperator(20);
			fail("Expected an IndexOutOfBoundsException to be thrown");
		} catch (IndexOutOfBoundsException e) {
			assertThat(e.getMessage(), is("Index: 20, Size: 2"));
		}
		
		try {
			com2AndExpr.getOperator(-10);
			fail("Expected an IndexOutOfBoundsException to be thrown");
		} catch (IndexOutOfBoundsException e) {
			assertThat(e.getMessage(), is("Index: -10, Size: 2"));
		}
		
		try {
			com2AndExpr.getOperator(0, 200);
			fail("Expected an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("The side \"200\" is unknown"));
		}
	}
	
	@Test
	public void getOperatorWithSideTest() {
		Assert.assertNull(sinAExpr.getOperator(0, Expression.SIDE_LEFT));
		Assert.assertNull(sinAExpr.getOperator(0, Expression.SIDE_RIGHT));
		
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
		
		com3AndExpr.setOperator(1, Expression.SIDE_LEFT, Expression.OR);
		Assert.assertEquals(Expression.OR, com3AndExpr.getOperator(1, Expression.SIDE_LEFT));
		
		com3OrExpr.setOperator(1, Expression.SIDE_LEFT, Expression.AND);
		Assert.assertEquals(Expression.AND, com3OrExpr.getOperator(1, Expression.SIDE_LEFT));
	}
	
	@Test
	public void setOperatorTestException() {
		// can not set an operator on the left side of the first expression, throw exception
		try {
			com2AndExpr.setOperator(0, Expression.SIDE_LEFT, Expression.AND);
			fail("Expected an java.lang.IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("Can not set an operator on the left side of the first sub-expression without an expression"));
		}
		
		
		// can not set an operator on the right side of the last expression, throw exception
		try {
			com2AndExpr.setOperator(1, Expression.SIDE_RIGHT, Expression.AND);
			fail("Expected an java.lang.IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("Can not set an operator on the right side of the last sub-expression without an expression"));
		}
		
		// if side option is an invalid value, throw exception
		try {
			com2AndExpr.setOperator(0, 200, Expression.AND);
			fail("Expected an java.lang.IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("The side \"200\" is unknown"));
		}
	}
	
	@Test
	public void addSubExpressionWithOperatorTest() {
		// add one expression on the right end of the sub-expression list
		// (if not specify the index, it will add it to the right end of the list)
		com2AndExpr.addSubExpressionWithOperator(new Expression("score", Expression.EQUAL, 98), Expression.OR);
		Assert.assertEquals(3, com2AndExpr.getNumberOfSubExpression());
		Assert.assertEquals(new Expression("firstName", Expression.EQUAL, "John"),    com2AndExpr.getSubExpression(0));
		Assert.assertEquals(Expression.AND, com2AndExpr.getOperator(0, Expression.SIDE_RIGHT));
		Assert.assertEquals(new Expression("age", Expression.EQUAL, 23),              com2AndExpr.getSubExpression(1));
		Assert.assertEquals(Expression.OR, com2AndExpr.getOperator(1, Expression.SIDE_RIGHT));
		Assert.assertEquals(new Expression("score", Expression.EQUAL, 98),             com2AndExpr.getSubExpression(2));
		
		// add one expression on the left end of the sub-expression list
		com2OrExpr.addSubExpressionWithOperator(0, new Expression("score", Expression.EQUAL, 99), Expression.AND);
		Assert.assertEquals(3, com2OrExpr.getNumberOfSubExpression());
		Assert.assertEquals(new Expression("score", Expression.EQUAL, 99),            com2OrExpr.getSubExpression(0));
		Assert.assertEquals(Expression.AND, com2OrExpr.getOperator(1, Expression.SIDE_LEFT));
		Assert.assertEquals(new Expression("firstName", Expression.EQUAL, "John"),    com2OrExpr.getSubExpression(1));
		Assert.assertEquals(Expression.OR, com2OrExpr.getOperator(2, Expression.SIDE_LEFT));
		Assert.assertEquals(new Expression("firstName", Expression.EQUAL, "Johnson"), com2OrExpr.getSubExpression(2));
		
		// add one expression on the right end of the sub-expression list
		// (if index is too large, it will still add on the right end rather than throwing an exception)
		com3OrExpr.addSubExpressionWithOperator(12, new Expression("score", Expression.EQUAL, 99), Expression.AND);
		Assert.assertEquals(4, com3OrExpr.getNumberOfSubExpression());
		Assert.assertEquals(new Expression("firstName", Expression.EQUAL, "John"),            com3OrExpr.getSubExpression(0));
		Assert.assertEquals(Expression.OR, com3OrExpr.getOperator(1, Expression.SIDE_LEFT));
		Assert.assertEquals(new Expression("firstName", Expression.EQUAL, "Johnson"),         com3OrExpr.getSubExpression(1));
		Assert.assertEquals(Expression.OR, com3OrExpr.getOperator(2, Expression.SIDE_LEFT));
		Assert.assertEquals(new Expression("firstName", Expression.EQUAL, "Johnny"),          com3OrExpr.getSubExpression(2));
		Assert.assertEquals(Expression.AND, com3OrExpr.getOperator(3, Expression.SIDE_LEFT));
		Assert.assertEquals(new Expression("score", Expression.EQUAL, 99),                    com3OrExpr.getSubExpression(3));
		
		// add one expression in the middle of the sub-expression list
		com3AndExpr.addSubExpressionWithOperator(1, new Expression("score", Expression.EQUAL, 99), Expression.OR);
		Assert.assertEquals(4, com3AndExpr.getNumberOfSubExpression());
		Assert.assertEquals(new Expression("firstName", Expression.EQUAL, "John"),              com3AndExpr.getSubExpression(0));
		Assert.assertEquals(Expression.OR, com3AndExpr.getOperator(0, Expression.SIDE_RIGHT));
		Assert.assertEquals(new Expression("score", Expression.EQUAL, 99),                      com3AndExpr.getSubExpression(1));
		Assert.assertEquals(Expression.AND, com3AndExpr.getOperator(1, Expression.SIDE_RIGHT));
		Assert.assertEquals(new Expression("age", Expression.EQUAL, 23),                        com3AndExpr.getSubExpression(2));
		Assert.assertEquals(Expression.AND, com3AndExpr.getOperator(2, Expression.SIDE_RIGHT));
		Assert.assertEquals(new Expression("score", Expression.EQUAL, 99),                      com3AndExpr.getSubExpression(3));
		
	}
	
	@Test
	public void addSubExpressionWithOperatorTestException() {		
		// This function is not applied to simple expression, throw exception
		try {
			sinAExpr.addSubExpressionWithOperator(new Expression("age", Expression.EQUAL, 23), Expression.AND);
			fail("Expected an java.lang.IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("This method is not valid for any simple expression. A simple expression needs to be compounded before adding a new sub-expression."));
		}
		
		// This function can not accept null as expression input parameter, throw exception
		try {
			com2OrExpr.addSubExpressionWithOperator(null, Expression.AND);
			fail("Expected an java.lang.IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("Expression cannot be null."));
		}
		
		// This function can not accept null as operator input parameter, throw exception
		try {
			com2OrExpr.addSubExpressionWithOperator(new Expression("age", Expression.EQUAL, 23), null);
			fail("Expected an java.lang.IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("The operator cannot be null."));
		}
		
		// if boolean operator is an invalid value, throw exception
		try {
			com2OrExpr.addSubExpressionWithOperator(new Expression("age", Expression.EQUAL, 23), "dummy string");
			fail("Expected an java.lang.IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("Operator must be either Expression.AND or Expression.OR."));
		}
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
	
	@Test
	public void addCompoundExpressionTestException() {
		// This function is not applied to simple expression, throw exception
		try {
			sinAExpr.addCompoundExpression(com2OrExpr, Expression.AND);
			fail("Expected an java.lang.IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("This method is not valid for any simple expression. A simple expression needs to be compounded before adding a new sub-expression."));
		}
		
		// This function can not accept null as expression input parameter, throw exception
		try {
			com2OrExpr.addCompoundExpression(null, Expression.AND);
			fail("Expected an java.lang.IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("Expression cannot be null."));
		}
	}
	
	@Test
	public void removeSubExpressionTest() {
		// remove sub-expression from a simple expression.
		sinAExpr.removeSubExpression(0);
		Assert.assertEquals(new Subject("firstName", null), sinAExpr.getSubject());
		Assert.assertEquals(Expression.EQUAL, sinAExpr.getOperator());
		Assert.assertEquals("John", sinAExpr.getValue());
		Assert.assertFalse(sinAExpr.isComplement());
		
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
	
	@Test
	public void findSubExpressionTest() {
		// case for can find
		Expression targetedExpr1 = comMultiLevelExpr.findSubExpression("score");
		Assert.assertEquals(99, targetedExpr1.getValue());
		
		// case for can not find
		Expression targetedExpr2 = comMultiLevelExpr.findSubExpression("school");
		Assert.assertNull(targetedExpr2);
		
		// put null as input subject
		Assert.assertNull(comMultiLevelExpr.findSubExpression(null));
	}
	
	@Test
	public void complement() {
		// complement a simple expression
		System.out.println(sinAExpr.toString());
		Expression sinAExprComplement = sinAExpr.complement(true);
		Assert.assertEquals(0, sinAExprComplement.getNumberOfSubExpression());
		System.out.println(sinAExprComplement.toString());
		System.out.println();
		
		// complement a compound expression (2 sub-expressions)
		System.out.println(com2AndExpr.toString());
		Expression com2AndExprComplement = com2AndExpr.complement(true);
		Assert.assertEquals(2, com2AndExprComplement.getNumberOfSubExpression());
		System.out.println(com2AndExprComplement.toString());
		System.out.println();
		
		// complement a compound expression (2 sub-expressions)
		System.out.println(com2OrExpr.toString());
		Expression com2OrExprComplement = com2OrExpr.complement(true);
		Assert.assertEquals(2, com2OrExprComplement.getNumberOfSubExpression());
		System.out.println(com2OrExprComplement.toString());
		System.out.println();
		
		// complement a compound expression (3 sub-expressions)
		System.out.println(com3AndExpr.toString());
		Expression com3AndExprComplement = com3AndExpr.complement(true);
		Assert.assertEquals(3, com3AndExprComplement.getNumberOfSubExpression());
		System.out.println(com3AndExprComplement.toString());
		System.out.println();
		
		// complement a compound expression (more complicated expression)
		System.out.println(comMultiLevelExpr.toString());
		Assert.assertEquals(3, comMultiLevelExpr.getNumberOfSubExpression());
		Expression complementExpr2 = comMultiLevelExpr.complement(true);
		Assert.assertEquals(3, complementExpr2.getNumberOfSubExpression());
		System.out.println(complementExpr2.toString());
		System.out.println();
		
		// complement one simple expression but nested in multiple level
		System.out.println(nestedExpr.toString());
		Expression nestedExprComplement = nestedExpr.complement(true);
		Assert.assertEquals(0, nestedExprComplement.getNumberOfSubExpression());
		System.out.println(nestedExprComplement.toString());
		System.out.println();
	}
	
	
	
	@Test
	public void resetTest() {
		// test simple expression
		sinAExpr.reset();
		Assert.assertNull(sinAExpr.getSubject());
		Assert.assertNull(sinAExpr.getOperator());
		Assert.assertNull(sinAExpr.getValue());
		Assert.assertFalse(sinAExpr.isComplement());
		Assert.assertEquals(0, sinAExpr.getNumberOfSubExpression());
		
		// test compound expression
		com3AndOrExpr.reset();
		Assert.assertNull(com3AndOrExpr.getSubject());
		Assert.assertNull(com3AndOrExpr.getOperator());
		Assert.assertNull(com3AndOrExpr.getValue());
		Assert.assertFalse(com3AndOrExpr.isComplement());
		Assert.assertEquals(0, com3AndOrExpr.getNumberOfSubExpression());
	}
	
	@Test
	public void toStringTest() {
		// test a compound expression (with 3 sub-expressions)
		Assert.assertEquals("(([firstName]==\"John\") && ([age]==23) && ([score]==99))", com3AndExpr.toString());
		
		// test a compound expression (more complicated expression)
		Assert.assertEquals("((([firstName]==\"John\") && ([age]==23)) || (([firstName]==\"Mary\") && ([age]==24) && ([score]==99)) || (([firstName]==\"Tony\") && ([age]==25) && ([lastName]==\"Lee\")))", comMultiLevelExpr.toString());
	
		// test an expression whose subject has value
		Assert.assertEquals("([firstName = FIRSTNAME]==\"John\")", new Expression(new Subject("firstName", "FIRSTNAME"), Expression.EQUAL, "John").toString());
		
		// test an expression which value is a list of string
		List<String> valueList1 = Arrays.asList("John", "Johnny", "Johnson");
		Assert.assertEquals("([firstName]==[\"John\",\"Johnny\",\"Johnson\"])", new Expression("firstName", Expression.EQUAL, valueList1).toString());
		
		// test an expression which value is a list of non-string object
		List<StringBuilder> valueList2 = Arrays.asList(new StringBuilder("John"), new StringBuilder("Johnny"), new StringBuilder("Johnson"));
		Assert.assertEquals("([firstName]==[John,Johnny,Johnson])", new Expression("firstName", Expression.EQUAL, valueList2).toString());
	}
	
	@Test
	public void minimizedTest() {
		// TODO need to fix the ExpressionEngine
	}
	
	@Test
	public void prefixTest() {
		// prefix simple expression: A => A
		List<Object> traverseList1 = new ArrayList<>();
		sinAExpr.prefix(node -> {
            if(node instanceof Expression) {
                traverseList1.add((Expression) node);
            } else if (node instanceof String) {
            	traverseList1.add((String) node);
            }
        });
		Assert.assertEquals(1, traverseList1.size());
		assertThat(traverseList1, Matchers.hasItems(
				new Expression("firstName", Expression.EQUAL, "John")
        ));
		
		// prefix compound expression: A && B => &&, A, B
		List<Object> traverseList2 = new ArrayList<>();
		com2AndExpr.prefix(node -> {
            if(node instanceof Expression) {
                traverseList2.add((Expression) node);
            } else if (node instanceof String) {
            	traverseList2.add((String) node);
            }
        });
		Assert.assertEquals(3, traverseList2.size());
		Assert.assertEquals(Expression.AND,                                        traverseList2.get(0));
		Assert.assertEquals(new Expression("firstName", Expression.EQUAL, "John"), traverseList2.get(1));
		Assert.assertEquals(new Expression("age", Expression.EQUAL, 23),           traverseList2.get(2));
		
		
		// prefix compound expression: A || (B  && C) => ||, A, &&, B, C
		List<Object> traverseList3 = new ArrayList<>();
		Expression compoundExpr3 = new Expression("AAA", Expression.EQUAL, "aaa").or(new Expression("BBB", Expression.EQUAL, "bbb").and("CCC", Expression.EQUAL, "ccc"));
		compoundExpr3.prefix(node -> {
            if(node instanceof Expression) {
                traverseList3.add((Expression) node);
            } else if (node instanceof String) {
            	traverseList3.add((String) node);
            }
        });
		Assert.assertEquals(5, traverseList3.size());
		Assert.assertEquals(Expression.OR, traverseList3.get(0));
		Assert.assertEquals(new Expression("AAA", Expression.EQUAL, "aaa"), traverseList3.get(1));
		Assert.assertEquals(Expression.AND, traverseList3.get(2));
		Assert.assertEquals(new Expression("BBB", Expression.EQUAL, "bbb"), traverseList3.get(3));
		Assert.assertEquals(new Expression("CCC", Expression.EQUAL, "ccc"), traverseList3.get(4));
	}
	
	@Test
	public void equalsSimpleExpressionTest() {
		// happy path
		Assert.assertTrue(sinAExpr.equals(new Expression("firstName", Expression.EQUAL, "John")));
		
		// compare with null object
		Expression exprNull = null;
		Assert.assertFalse(sinAExpr.equals(exprNull));
		
		// compare with non-expression object
		Assert.assertFalse(sinAExpr.equals(new StringBuilder()));
		
		// value is different
		Assert.assertFalse(sinAExpr.equals(new Expression("firstName", Expression.EQUAL, "Johnny")));
		
		// operator is different
		Assert.assertFalse(sinAExpr.equals(new Expression("firstName", Expression.ENDS_WITH, "John")));
		
		// subject is null
		Assert.assertFalse(new Expression((Subject) null, Expression.EQUAL, "John").equals(new Expression("firstName", Expression.EQUAL, "John")));
		Assert.assertFalse(new Expression("firstName", Expression.EQUAL, "John").equals(new Expression((Subject) null, Expression.EQUAL, "John")));
		
		// operator is null
		Assert.assertFalse(new Expression("firstName", null, "John").equals(new Expression("firstName", Expression.EQUAL, "John")));
		Assert.assertFalse(new Expression("firstName", Expression.EQUAL, "John").equals(new Expression("firstName", null, "John")));
		
		// value is null
		Assert.assertFalse(new Expression("firstName", Expression.EQUAL, null).equals(new Expression("firstName", Expression.EQUAL, "John")));
		Assert.assertFalse(new Expression("firstName", Expression.EQUAL, "John").equals(new Expression("firstName", Expression.EQUAL, null)));
	}
	
	@Test
	public void equalsCompoundExpressionTest() {
		Assert.assertTrue(comMultiLevelExpr.equals(new Expression(new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23))
				.or(new Expression("firstName", Expression.EQUAL, "Mary").and("age", Expression.EQUAL, 24).and("score", Expression.EQUAL, 99))
				.or(new Expression("firstName", Expression.EQUAL, "Tony").and("age", Expression.EQUAL, 25).and("lastName", Expression.EQUAL, "Lee"))));
		
		// different number of sub-expressions
		Expression expr1A = new Expression("firstName", Expression.EQUAL, "Mary").and("age", Expression.EQUAL, 29).and("score", Expression.EQUAL, 99);
		Expression expr1B = new Expression("firstName", Expression.EQUAL, "Mary").and("age", Expression.EQUAL, 29);
		Assert.assertFalse(expr1A.equals(expr1B));
		Assert.assertFalse(expr1B.equals(expr1A));
		
		// one sub-expression is null
		Expression expr2A = new Expression("firstName", Expression.EQUAL, "Mary").and("age", Expression.EQUAL, 29).and("score", Expression.EQUAL, 99);
		Expression expr2B = new Expression("firstName", Expression.EQUAL, "Mary").and("age", Expression.EQUAL, 29).and("score", Expression.EQUAL, 99);
		expr2B.replaceSubExpression(1, null);
		Assert.assertFalse(expr2A.equals(expr2B));
		Assert.assertFalse(expr2B.equals(expr2A));
		
		// one operator is different
		Expression expr3A = new Expression("firstName", Expression.EQUAL, "Mary").and("age", Expression.EQUAL, 29).or("score", Expression.EQUAL, 99);
		Expression expr3B = new Expression("firstName", Expression.EQUAL, "Mary").and("age", Expression.EQUAL, 29).and("score", Expression.EQUAL, 99);
		Assert.assertFalse(expr3A.equals(expr3B));
		Assert.assertFalse(expr3B.equals(expr3A));
		
		// one operator is null
		Expression expr4A = new Expression("firstName", Expression.EQUAL, "Mary").and("age", Expression.EQUAL, 29).and("score", Expression.EQUAL, 99);
		Expression expr4B = new Expression("firstName", Expression.EQUAL, "Mary").and("age", Expression.EQUAL, 29).and("score", Expression.EQUAL, 99);
		expr4B.setOperator(1, Expression.SIDE_RIGHT, null);
		Assert.assertFalse(expr4A.equals(expr4B));
		Assert.assertFalse(expr4B.equals(expr4A));
		
		// value of a certain sub-expression is different
		Assert.assertFalse(comMultiLevelExpr.equals(new Expression(new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23))
				.or(new Expression("firstName", Expression.EQUAL, "Mary").and("age", Expression.EQUAL, 29).and("score", Expression.EQUAL, 99))   // age is not same
				.or(new Expression("firstName", Expression.EQUAL, "Tony").and("age", Expression.EQUAL, 25).and("lastName", Expression.EQUAL, "Lee"))));
	}
	
	@Test 
	public void cloneTest() {
		Expression newSinExpr = (Expression) sinAExpr.clone();
		newSinExpr.and("age", Expression.EQUAL, 33);
		Assert.assertTrue(!sinAExpr.isCompound());
		
		Expression newComMultiLevelExpr = (Expression) comMultiLevelExpr.clone();
		newComMultiLevelExpr.getSubExpression(1).setOperator(Expression.ENDS_WITH);
		Assert.assertTrue(comMultiLevelExpr.equals(new Expression(new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23))
				.or(new Expression("firstName", Expression.EQUAL, "Mary").and("age", Expression.EQUAL, 24).and("score", Expression.EQUAL, 99))
				.or(new Expression("firstName", Expression.EQUAL, "Tony").and("age", Expression.EQUAL, 25).and("lastName", Expression.EQUAL, "Lee"))));
	}
}
