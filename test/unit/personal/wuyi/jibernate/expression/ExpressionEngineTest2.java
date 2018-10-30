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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Test class for {@code ExpressionEngine}.
 * 
 * @author  Wuyi Chen
 * @date    09/18/2018
 * @version 1.0
 * @since   1.0
 */
public class ExpressionEngineTest2 {
	// AB + CD
	private Expression comSop1 = new Expression(new Expression("A", Expression.EQUAL, "a").and("B", Expression.EQUAL, "b"))
			.or(new Expression("C", Expression.EQUAL, "c").and("D", Expression.EQUAL, "d"));
	
	// (A + B)(C + D)
	private Expression comSop2 = new Expression(new Expression("A", Expression.EQUAL, "a").or("B", Expression.EQUAL, "b"))
			.and(new Expression("C", Expression.EQUAL, "c").or("D", Expression.EQUAL, "d"));
	
	// (A + B)(C + D)(E + F)
	private Expression comSop3 = new Expression(new Expression("A", Expression.EQUAL, "a").or("B", Expression.EQUAL, "b"))
			.and(new Expression("C", Expression.EQUAL, "c").or("D", Expression.EQUAL, "d"))
			.and(new Expression("E", Expression.EQUAL, "e").or("F", Expression.EQUAL, "f"));
	
	// !(A + B)(C + D)
	private Expression comSop4 = new Expression(new Expression("A", Expression.EQUAL, "a").or("B", Expression.EQUAL, "b").complement())
			.and(new Expression("C", Expression.EQUAL, "c").or("D", Expression.EQUAL, "d"));
	
	@Test
	public void getSumOfProductsTest() {
		// Case 1: AB + CD ==> AB + CD
		// if an expression is already in sop format, so the expression should be no change
		System.out.println("Original Expr:     " + comSop1);
		System.out.println("By Stack:          " + ExpressionEngine.getSumOfProductsByStack(comSop1));
		System.out.println("By Divide&Conquor: " + ExpressionEngine.getSumOfProductsByDivideAndConquor(comSop1, 3));
		Assert.assertEquals(ExpressionEngine.getSumOfProductsByStack(comSop1), ExpressionEngine.getSumOfProductsByDivideAndConquor(comSop1, 3));
		
		// Case 2: (A + B)(C + D) ==> AC + AD + BC + BD
		System.out.println("Original Expr:     " + comSop2);
		System.out.println("By Stack:          " + ExpressionEngine.getSumOfProductsByStack(comSop2));
		System.out.println("By Divide&Conquor: " + ExpressionEngine.getSumOfProductsByDivideAndConquor(comSop2, 2));
		Assert.assertEquals(ExpressionEngine.getSumOfProductsByStack(comSop2), ExpressionEngine.getSumOfProductsByDivideAndConquor(comSop2, 2));
		
		// Case 3: (A + B)(C + D)(E + F) ==> ACE + ADE + BCE + BDE + ACF + ADF + BCF + BDF
		System.out.println("Original Expr:     " + comSop3);
		System.out.println("By Stack:          " + ExpressionEngine.getSumOfProductsByStack(comSop3));
		System.out.println("By Divide&Conquor: " + ExpressionEngine.getSumOfProductsByDivideAndConquor(comSop3, 2));
		Assert.assertEquals(ExpressionEngine.getSumOfProductsByStack(comSop3), ExpressionEngine.getSumOfProductsByDivideAndConquor(comSop3, 2));
		
		// Case 4: !(A + B)(C + D) ==> !A * !B (C + D) ==> !A!BC + !A!BD
		System.out.println("Original Expr:     " + comSop4);
		System.out.println("By Stack:          " + ExpressionEngine.getSumOfProductsByStack(comSop4));
		System.out.println("By Divide&Conquor: " + ExpressionEngine.getSumOfProductsByDivideAndConquor(comSop4, 2));
		Assert.assertEquals(ExpressionEngine.getSumOfProductsByStack(comSop4), ExpressionEngine.getSumOfProductsByDivideAndConquor(comSop4, 2));
	}
	
	@Test
	public void getMintermsTest() {
		// Case 1: AB + CD ==> {AB, CD}
		assertThat(ExpressionEngine.getMinterms(comSop1), containsInAnyOrder(
				new Expression("A", Expression.EQUAL, "a").and("B", Expression.EQUAL, "b"),
				new Expression("C", Expression.EQUAL, "c").and("D", Expression.EQUAL, "d"))
        );
		
		// Case 2: (A + B)(C + D) ==> {AC, AD, BC, BD}
		assertThat(ExpressionEngine.getMinterms(comSop2), containsInAnyOrder(
				new Expression("A", Expression.EQUAL, "a").and("C", Expression.EQUAL, "c"),
				new Expression("A", Expression.EQUAL, "a").and("D", Expression.EQUAL, "d"),
				new Expression("B", Expression.EQUAL, "b").and("C", Expression.EQUAL, "c"),
				new Expression("B", Expression.EQUAL, "b").and("D", Expression.EQUAL, "d"))
        );
		
		// Case 3: (A + B)(C + D)(E + F) ==> {ACE, ADE, BCE, BDE, ACF, ADF, BCF, BDF}
		assertThat(ExpressionEngine.getMinterms(comSop3), containsInAnyOrder(
				new Expression("A", Expression.EQUAL, "a").and("C", Expression.EQUAL, "c").and("E", Expression.EQUAL, "e"),
				new Expression("A", Expression.EQUAL, "a").and("C", Expression.EQUAL, "c").and("F", Expression.EQUAL, "f"),
				new Expression("A", Expression.EQUAL, "a").and("D", Expression.EQUAL, "d").and("E", Expression.EQUAL, "e"),
				new Expression("A", Expression.EQUAL, "a").and("D", Expression.EQUAL, "d").and("F", Expression.EQUAL, "f"),
				new Expression("B", Expression.EQUAL, "b").and("C", Expression.EQUAL, "c").and("E", Expression.EQUAL, "e"),
				new Expression("B", Expression.EQUAL, "b").and("C", Expression.EQUAL, "c").and("F", Expression.EQUAL, "f"),
				new Expression("B", Expression.EQUAL, "b").and("D", Expression.EQUAL, "d").and("E", Expression.EQUAL, "e"),
				new Expression("B", Expression.EQUAL, "b").and("D", Expression.EQUAL, "d").and("F", Expression.EQUAL, "f"))
        );
		
		// Case 4: !(A + B)(C + D) ==> {!A!BC, !A!BD}
		assertThat(ExpressionEngine.getMinterms(comSop4), containsInAnyOrder(
				new Expression(new Expression("A", Expression.EQUAL, "a").complement()).and(new Expression("B", Expression.EQUAL, "b").complement()).and("C", Expression.EQUAL, "c"),
				new Expression(new Expression("A", Expression.EQUAL, "a").complement()).and(new Expression("B", Expression.EQUAL, "b").complement()).and("D", Expression.EQUAL, "d"))
        );
	}
}
