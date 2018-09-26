package personal.wuyi.jibernate.transformer;

import personal.wuyi.jibernate.entity.Uri;
import personal.wuyi.jibernate.expression.Expression;
import personal.wuyi.jibernate.expression.ExpressionTransformer;
import personal.wuyi.jibernate.expression.Subject;

/**
 * Uri Expression Transformer.
 * 
 * <p>This class is to transform the expression if the subject is "uri" so 
 * then change "uri" to "id" and get the primary key from URI string.
 * 
 * <p>For example:
 * <pre>
 *     Expression("uri","=","/personal/wuyi/jibernate/entity/Student/27") ==> Expression("id", "=", "27")
 * </pre>
 * 
 * @author  Wuyi Chen
 * @date    09/25/2018
 * @version 1.0
 * @since   1.0
 */
public class UriExpressionTransformer extends ExpressionTransformer {
    @Override
    public Expression transform(Subject subject, String operator, Object value) {
        if ("uri".equals(subject.getName()) && value != null) {
        	subject = new Subject(getAttribute());
        	if (value instanceof String) {
        		value = Uri.parse((String) value);
        	}
        	value = ((Uri) value).getId();
        }

        return super.transform(subject, operator, value);
    }

    /**
     * The name of the unique identifier.
     *
     * @return  The name of the unique identifier.
     * 
     * @since   1.0
     */
    protected String getAttribute() {
        return "id";
    }
}
