package personal.wuyi.jibernate.expression;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


@RunWith(Parameterized.class)
public class ExpressionTest2 {	
	String leftOperator;
	String rightOperator;
	Expression a = new Expression("aaa", Expression.EQUAL, "111");
	Expression b = new Expression("bbb", Expression.EQUAL, "222");
	Expression c = new Expression("ccc", Expression.EQUAL, "333");
	Expression expectA;
	Expression expectB;
	Expression expectC;
	
	public ExpressionTest2(String leftOperator, String rightOperator) {
	    this.leftOperator  = leftOperator;
	    this.rightOperator = rightOperator;
	}
	
	@Parameters
	public static Iterable<Object[]> data() {
	    return Arrays.asList(new Object[][] {
	        { null,           null,           new Expression("aaa", Expression.EQUAL, "111").and("ccc", Expression.EQUAL, "333"), new Expression("bbb", Expression.EQUAL, "222"), new Expression("ccc", Expression.EQUAL, "333")},
	        { null,           Expression.AND, new Expression("aaa", Expression.EQUAL, "111"),   },
	        { null,           Expression.OR,  new Expression("aaa", Expression.EQUAL, "111")            },
	        { Expression.AND, null,           new Expression("aaa", Expression.EQUAL, "111")           },
	        { Expression.AND, Expression.AND, new Expression("aaa", Expression.EQUAL, "111") },
	        { Expression.AND, Expression.OR,  new Expression("aaa", Expression.EQUAL, "111")  },
	        { Expression.OR,  null,           new Expression("aaa", Expression.EQUAL, "111")            },
	        { Expression.OR,  Expression.AND, new Expression("aaa", Expression.EQUAL, "111")  }, 
	        { Expression.OR,  Expression.OR,  new Expression("aaa", Expression.EQUAL, "111")   }
	    });
	}
	
	@Test
	public void applyDeMorganLawTest() {	
		Expression.applyDeMorganLaw(a, b, c, leftOperator, rightOperator);
		
		// case 1: null E null	
		Assert.assertEquals(a, new Expression("aaa", Expression.EQUAL, "111").and("ccc", Expression.EQUAL, "333"));
		
		// case 2: null E and
		// case 3: null E or
		// case 4: and  E null
		// case 5: and  E and
		// case 6: and  E or
		// case 7: or   E null
		// case 8: or   E and 
		// case 9: or   E or
	}
}
