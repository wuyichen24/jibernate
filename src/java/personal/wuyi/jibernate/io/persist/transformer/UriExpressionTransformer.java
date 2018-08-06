package personal.wuyi.jibernate.io.persist.transformer;

import personal.wuyi.jibernate.io.persist.core.Uri;
import personal.wuyi.jibernate.io.persist.expression.Expression;
import personal.wuyi.jibernate.io.persist.expression.ExpressionTransformer;
import personal.wuyi.jibernate.io.persist.expression.Subject;

/**
 * Uri Expression Transformer
 * 
 * @author  Wuyi Chen
 */
public class UriExpressionTransformer extends ExpressionTransformer {
    /**
     * Transform any GH specific expression values into vanilla SQL
     * 
     * <p>Example:
     *   ([A]STARTS_WITH "foo") => ([A] LIKE "%foo")
     *
     * @param expression
     * @return
     */
    @Override
    public Expression transform(Subject subject, String predicate, Object value) {
        if ("uri".equals(subject.getName()) && value != null) {
        	subject = new Subject(getAttribute());
        	if (value instanceof String) {
        		value = Uri.parse((String) value);
        	}
        	value = ((Uri) value).getId();
        }

        return super.transform(subject, predicate, value);
    }

    /**
     * Determines the URI "id" attribute name. By default this is assumed to 
     * be "id".
     *
     * @return
     */
    protected String getAttribute() {
        return "id";
    }
}
