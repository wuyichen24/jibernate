package personal.wuyi.jibernate.transformer;

import personal.wuyi.jibernate.expression.Expression;
import personal.wuyi.jibernate.expression.ExpressionTransformer;
import personal.wuyi.jibernate.expression.Subject;

/**
 * Search Expression Transformer.
 *
 * <p>This class is to transform the expression if the operator is using 
 * "START_WITH", "END_WITH" or "CONTAINS", they will be replaced by "LIKE" 
 * with %. For example:
 * <pre>
 *   START_WITH 'ABC' ==> LIKE 'ABC%'
 *   END_WITH 'ABC'   ==> LIKE '%ABC'
 *   CONTAINS 'ABC'   ==> LIKE '%ABC%'
 * </pre>
 * 
 * @author  Wuyi Chen
 * @date    09/26/2018
 * @version 1.0
 * @since   1.0
 */
public class SearchExpressionTransformer extends ExpressionTransformer {
    @Override
    public Expression transform(Subject subject, String operator, Object value) {
    	switch (operator) {
        	case Expression.STARTS_WITH : return super.transform(subject, "LIKE", value + "%");
        	case Expression.ENDS_WITH   : return super.transform(subject, "LIKE", "%" + value);
        	case Expression.CONTAINS    : return super.transform(subject, "LIKE", "%" + value + "%");
        	default: return super.transform(subject, operator, value);
    	}
    }
}
