package personal.wuyi.autostock.io.persist.transformer;

import personal.wuyi.autostock.io.persist.expression.Expression;
import personal.wuyi.autostock.io.persist.expression.ExpressionTransformer;
import personal.wuyi.autostock.io.persist.expression.Subject;

/**
 * Search Expression Transformer
 *
 * <p>Handle transformation for SQL "search" expressions, e.g. starts with, 
 * ends with, contains.
 * 
 * @author  Wuyi Chen
 */
public class SearchExpressionTransformer extends ExpressionTransformer {
    /**
     * Transform any GH specific expression values into vanilla SQL
     * Example:
     *   ([A]STARTS_WITH "foo") => ([A] LIKE "foo%")
     *
     * @param expression
     * @return
     */
    @Override
    public Expression transform(Subject subject, String predicate, Object value) {
        if (Expression.STARTS_WITH.equalsIgnoreCase(predicate)) {
            predicate = "LIKE";
            value = value + "%";
        } else if (Expression.ENDS_WITH.equalsIgnoreCase(predicate)) {
            predicate = "LIKE";
            value = "%" + value;
        } else if (Expression.CONTAINS.equalsIgnoreCase(predicate)) {
            predicate = "LIKE";
            value = "%" + value + "%";
        }

        return super.transform(subject, predicate, value);
    }
}
