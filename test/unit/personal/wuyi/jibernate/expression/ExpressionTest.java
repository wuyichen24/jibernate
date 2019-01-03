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

import org.junit.Assert;
import org.junit.Test;

import personal.wuyi.jibernate.entity.Student;
import personal.wuyi.jibernate.entity.Uri;

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
		try {
			com2AndExpr.setOperator(0, Expression.SIDE_LEFT, Expression.AND);
			fail("Expected an java.lang.IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("Can not set an operator on the left side of the first sub-expression without an expression"));
		}
		
		try {
			com2AndExpr.setOperator(1, Expression.SIDE_RIGHT, Expression.AND);
			fail("Expected an java.lang.IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("Can not set an operator on the right side of the last sub-expression without an expression"));
		}
		
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
	
	@Test(expected = IllegalArgumentException.class)
	public void addSubExpressionWithOperatorTestException() {
		// check if a simple expression to call this method, this method will throw exception
		sinAExpr.addSubExpressionWithOperator(new Expression("age", Expression.EQUAL, 23), Expression.AND);
		Assert.assertEquals(1, sinAExpr.getNumberOfSubExpression());
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
		sinAExpr.addCompoundExpression(com2OrExpr, Expression.AND);
		Assert.assertEquals(3, sinAExpr.getNumberOfSubExpression());
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
	}
	
	@Test
	public void complement() {
		System.out.println(sinAExpr.toString());
		Expression sinExprComplement = sinAExpr.complement(true);
		Assert.assertEquals(0, sinExprComplement.getNumberOfSubExpression());
		System.out.println(sinExprComplement.toString());
		System.out.println();
		
		System.out.println(com2AndExpr.toString());
		Expression com2AndExprComplement = com2AndExpr.complement(true);
		Assert.assertEquals(2, com2AndExprComplement.getNumberOfSubExpression());
		System.out.println(com2AndExprComplement.toString());
		System.out.println();
		
		System.out.println(com2OrExpr.toString());
		Expression com2OrExprComplement = com2OrExpr.complement(true);
		Assert.assertEquals(2, com2OrExprComplement.getNumberOfSubExpression());
		System.out.println(com2OrExprComplement.toString());
		System.out.println();
		
		System.out.println(com3AndExpr.toString());
		Expression com3AndExprComplement = com3AndExpr.complement(true);
		Assert.assertEquals(3, com3AndExprComplement.getNumberOfSubExpression());
		System.out.println(com3AndExprComplement.toString());
		System.out.println();
		
		System.out.println(comMultiLevelExpr.toString());
		Assert.assertEquals(3, comMultiLevelExpr.getNumberOfSubExpression());
		Expression complementExpr2 = comMultiLevelExpr.complement(true);
		Assert.assertEquals(3, complementExpr2.getNumberOfSubExpression());
		System.out.println(complementExpr2.toString());
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
		Assert.assertEquals("(([firstName]==\"John\") && ([age]==23) && ([score]==99))", com3AndExpr.toString());
		Assert.assertEquals("((([firstName]==\"John\") && ([age]==23)) || (([firstName]==\"Mary\") && ([age]==24) && ([score]==99)) || (([firstName]==\"Tony\") && ([age]==25) && ([lastName]==\"Lee\")))", comMultiLevelExpr.toString());
	}
	
	@Test
	public void minimizedTest() {
		// TODO need to fix the ExpressionEngine
	}
	
	@Test
	public void prefixTest() {
		// TODO need to know how to use Consumer
	}
	
	@Test
	public void equalTest() {
		Assert.assertTrue(sinAExpr.equals(new Expression("firstName", Expression.EQUAL, "John")));
		Assert.assertFalse(sinAExpr.equals(new Expression("firstName", Expression.EQUAL, "Johnny")));
		Assert.assertFalse(sinAExpr.equals(new Expression("firstName", Expression.ENDS_WITH, "John")));
		
		Assert.assertTrue(comMultiLevelExpr.equals(new Expression(new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23))
				.or(new Expression("firstName", Expression.EQUAL, "Mary").and("age", Expression.EQUAL, 24).and("score", Expression.EQUAL, 99))
				.or(new Expression("firstName", Expression.EQUAL, "Tony").and("age", Expression.EQUAL, 25).and("lastName", Expression.EQUAL, "Lee"))));
		Assert.assertFalse(comMultiLevelExpr.equals(new Expression(new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23))
				.or(new Expression("firstName", Expression.EQUAL, "Mary").and("age", Expression.EQUAL, 29).and("score", Expression.EQUAL, 99))
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
