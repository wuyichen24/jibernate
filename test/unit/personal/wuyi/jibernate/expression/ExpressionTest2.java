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

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test class for {@code Expression}.
 * 
 * @author  Wuyi Chen
 * @date    09/18/2018
 * @version 1.0
 * @since   1.0
 */
@RunWith(Parameterized.class)
public class ExpressionTest2 {	
	private String leftOperator;
	private String rightOperator;
	private Expression a = new Expression("aaa", Expression.EQUAL, "111");
	private Expression b = new Expression("bbb", Expression.EQUAL, "222");
	private Expression c = new Expression("ccc", Expression.EQUAL, "333");
	private Expression expectA;
	private Expression expectB;
	Expression expectC;
	
	public ExpressionTest2(String leftOperator, String rightOperator, Expression expectA, Expression expectB, Expression expectC) {
	    this.leftOperator  = leftOperator;
	    this.rightOperator = rightOperator;
	    this.expectA       = expectA;
	    this.expectB       = expectB;
	    this.expectC       = expectC;
	}
	
	@Parameters
	public static Iterable<Object[]> data() {
	    return Arrays.asList(new Object[][] {
	        { null,           null,           new Expression("aaa", Expression.EQUAL, "111").and("ccc", Expression.EQUAL, "333"), new Expression("bbb", Expression.EQUAL, "222"), new Expression("ccc", Expression.EQUAL, "333") },
	        { null,           Expression.OR,  new Expression("aaa", Expression.EQUAL, "111").and("ccc", Expression.EQUAL, "333"), new Expression("bbb", Expression.EQUAL, "222"), new Expression("ccc", Expression.EQUAL, "333") },
	        { Expression.AND, null,           new Expression("aaa", Expression.EQUAL, "111"), new Expression("bbb", Expression.EQUAL, "222").or("ccc", Expression.EQUAL, "333"), new Expression("ccc", Expression.EQUAL, "333") },
	        { Expression.AND, Expression.AND, new Expression("aaa", Expression.EQUAL, "111"), new Expression("bbb", Expression.EQUAL, "222").or("ccc", Expression.EQUAL, "333"), new Expression("ccc", Expression.EQUAL, "333") },
	        { Expression.AND, Expression.OR,  new Expression("aaa", Expression.EQUAL, "111"), new Expression("bbb", Expression.EQUAL, "222").or("ccc", Expression.EQUAL, "333"), new Expression("ccc", Expression.EQUAL, "333") },
	        { Expression.OR,  null,           new Expression("aaa", Expression.EQUAL, "111").and("ccc", Expression.EQUAL, "333"), new Expression("bbb", Expression.EQUAL, "222"), new Expression("ccc", Expression.EQUAL, "333") },
	        { Expression.OR,  Expression.OR,  new Expression("aaa", Expression.EQUAL, "111").and("ccc", Expression.EQUAL, "333"), new Expression("bbb", Expression.EQUAL, "222"), new Expression("ccc", Expression.EQUAL, "333") }
	    });
	}
	
	@Test
	public void applyDeMorganLawTest() {
		Expression.applyDeMorganLaw(a, b, c, leftOperator, rightOperator);
		
		Assert.assertEquals(a, expectA);
		Assert.assertEquals(b, expectB);
		Assert.assertEquals(c, expectC);
		
		// case 1: null E null	 -- case 1
		// case 2: null E and    -- not testable
		// case 3: null E or     -- case 1
		// case 4: and  E null   -- case 3
		// case 5: and  E and    -- case 3
		// case 6: and  E or     -- case 3
		// case 7: or   E null   -- case 1
		// case 8: or   E and    -- not testable
		// case 9: or   E or     -- case 1
	}
}
