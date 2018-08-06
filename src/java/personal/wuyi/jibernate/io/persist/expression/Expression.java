package personal.wuyi.autostock.io.persist.expression;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

import personal.wuyi.autostock.util.ReflectUtil2;
import personal.wuyi.autostock.util.StringUtil;

/**
 * Expression
 * 
 * <p>This class represents the where clause of the SQL statement
 * 
 * @author Wuyi Chen
 */
public class Expression implements Cloneable, Serializable {
    private static final long serialVersionUID = -1378805926311132288L;

    public static final String AND                = "&&";
    public static final String OR                 = "||";

    public static final String EQUAL              = "==";
    public static final String NOT_EQUAL          = "!=";
    public static final String GREATER_THAN       = ">";
    public static final String LESS_THAN          = "<";
    public static final String LESS_THAN_EQUAL    = "<=";
    public static final String GREATER_THAN_EQUAL = ">=";

    public static final String STARTS_WITH        = "STARTS_WITH";
    public static final String ENDS_WITH          = "ENDS_WITH";
    public static final String CONTAINS           = "CONTAINS";

    protected static int SIDE_LEFT  = 1;
    protected static int SIDE_RIGHT = 2;

    // by default complement is false, if true then opposite of expression value
    private boolean complement = false;

    private Subject subject   = null;
    private String  predicate = null;
    private Object  value     = null;

    private List<Object> expressionsAndOperators = null;

    /**
     * Constructs a {@code Expression}
     * 
     * <p>Use of the default constructor is discouraged due to ambiguity 
     * concerning whether the uninitialized expression is simple or compound.
     */
    protected Expression() {

    }

    /**
     * Constructs a {@code Expression}
     * 
     * <p>This function will create a new simple expression.
     *
     * @param subject
     * @param predicate
     * @param value
     */
    public Expression(String subject, String predicate, Object value) {
        this(new Subject(subject, null), predicate, value);
    }

    /**
     * Constructs a {@code Expression}
     * 
     * <p>This function will create a new simple expression.
     * 
     * @param subject
     * @param predicate
     * @param value
     */
    public Expression(Subject subject, String predicate, Object value) {
        setSubject(subject);
        setPredicate(predicate);
        setValue(value);
    }

    /**
     * Constructs a {@code Expression}
     * 
     * <p>Create a new compound expression with the argument sub-expression.
     *
     * @param expression
     */
    public Expression(Expression expression) {
        add(expression, null);
    }
    
    public Subject getSubject()                      { return subject;               }
    public void    setSubject(Subject subject)       { this.subject = subject;       }
    public String  getPredicate()                    { return predicate;             }
    public void    setPredicate(String predicate)    { this.predicate = predicate;   }
    public Object  getValue()                        { return value;                 }
    public void    setValue(Object value)            { this.value = value;           }
    public boolean isComplement()                    { return complement;            }
    public void    setComplement(boolean complement) { this.complement = complement; }

    /**
     * Check a expression is a simple statement or is compound.
     * 
     * <p>
     * E = subject + predicate + value
     * E = (E)
     * E = (E && E)
     * E = (E || E)
     *
     * @return
     */
    public boolean isCompound() {
        if (expressionsAndOperators == null || expressionsAndOperators.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Get the number of the sub-sub-expressions of a compound expression.
     */
    public int size() {
        if (expressionsAndOperators == null || expressionsAndOperators.size() == 0) {
            return (0);
        } else {
            return ((expressionsAndOperators.size() - 1) / 2) + 1;
        }
    }

    /**
     * Operate AND on 2 expressions
     * 
     * <p>Perform logical intersections by appending argument expression as a
     * sub-expression to the end of the current expression. If current 
     * expression is simple, it will compound() self prior to "ANDing" new 
     * sub-expression.
     *
     * @param expression
     * @return this
     */
    public Expression and(Expression expression) {
        if (!isCompound()) {
            compound();
        }
        add(expression, AND);
        return this;
    }


    /**
     * Operate AND on 2 expressions
     * 
     * <p>Perform logical intersections by appending argument expression as a
     * sub-expression to the end of the current expression. If current 
     * expression is simple, it will compound() self prior to "ANDing" new 
     * sub-expression.
     *
     * @param subject
     * @param predicate
     * @param value
     * @return
     */
    public Expression and(String subject, String predicate, Object value) {
        return and(new Expression(subject, predicate, value));
    }

    /**
     * Operate AND on multiple expressions
     * 
     * <p>Creates a new compound expression from the logical intersection 
     * of argument sub-expression(s).
     *
     * @param subExpressions
     * @return new expression
     */
    public static Expression and(Expression... subExpressions) {
        Expression expression = new Expression();
        
        for (int i = 0; i < subExpressions.length; i++) {
            expression.add(subExpressions[i], AND);
        }
        
        return expression;
    }

	/**
	 * Operate OR on 2 expressions
	 * 
	 * <p> Perform logical union by appending argument expression as a
	 * sub-expression to the end of the current expression. If current
	 * expression is simple, it will compound() self prior to "ORing" new
	 * sub-expression.
	 *
	 * @param expression
	 * @return this
	 */
    public Expression or(Expression expression) {
        if (!isCompound()) {
            compound();
        }
        add(expression, OR);
        return this;
    }


    /**
     * Operate OR on 2 expressions
     * 
	 * <p> Perform logical union by appending argument expression as a
	 * sub-expression to the end of the current expression. If current
	 * expression is simple, it will compound() self prior to "ORing" new
	 * sub-expression.
     *
     * @param subject
     * @param predicate
     * @param value
     * @return
     */
    public Expression or(String subject, String predicate, Object value) {
        return or(new Expression(subject, predicate, value));
    }


    /**
     * Operate OR on multiple expressions
     *
     * @param subExpressions
     * @return new expression
     */
    public static Expression or(Expression... subExpressions) {
        Expression expression = new Expression();

        for (int i = 0; i < subExpressions.length; i++) {
            expression.add(subExpressions[i], OR);
        }

        return expression;
    }


    /**
     * Compound the current expression
     * 
     * <p>Method will clone current expression, reset all current state, then 
     * nest cloned value as a sub-expression inside new compound structure.
     *
     * @return
     */
    public Expression compound() {
        // only compound if non-empty expression
        if (subject != null || predicate != null || value != null || expressionsAndOperators != null) {
            Expression subExpr = (Expression) this.clone();
            clear();
            add(subExpr, null);
        }

        return this;
    }


    /**
     * Get the sub-expression referenced by the specified index.
     *
     * <p>Expressions are on even numbers starting at 0.
     *
     * @param index
     * @return
     */
    public Expression getSubExpression(int index) {
        return (Expression) expressionsAndOperators.get(expressionIndexToArrayIndex(index));
    }
    
    /**
     * Replace the sub-expression existing at the specified index with the new one
     *
     * @param index
     * @param expression
     */
    protected void setSubExpression(int index, Expression expression) {
        expressionsAndOperators.set(expressionIndexToArrayIndex(index), expression);
    }

    /**
     * Get the operator 
     * 
     * <p>The operator is referenced by the specified expression index if 
     * operator exists to the immediate left of the expression.
     *
     * @param index
     * @return
     */
    public String getOperator(int index) {
        return getOperator(index, SIDE_LEFT);
    }

    /**
     * Get the operator
     * 
     * <p>The operator referenced by the specified expression index where side 
     * specifies the operator existing to the immediate left or right of the 
     * expression.
     *
     * @param index
     * @param side
     * @return
     */
    protected String getOperator(int index, int side) {
        if ((side != SIDE_LEFT) && (side != SIDE_RIGHT)) {
            throw new IllegalArgumentException("The side \"" + side + "\" is unknown");
        }

        String operator = null;

        // There is no operator left of the first expression or right of the 
        // last expression.
        if (side == SIDE_LEFT) {
            int realIndex = expressionIndexToArrayIndex(index) - 1;
            if (realIndex > 0) {
                operator = (String) expressionsAndOperators.get(realIndex);
            }
        } else {
            int realIndex = expressionIndexToArrayIndex(index) + 1;
            if (realIndex < expressionsAndOperators.size()) {
                operator = (String) expressionsAndOperators.get(realIndex);
            }
        }

        return operator;
    }
    
    /**
     * Replace the specified operator with the new one.
     *
     * @param index
     * @param side
     * @param operator
     */
    protected void setOperator(int index, int side, String operator) {

        // Validate.
        if ((side != SIDE_LEFT) || (side != SIDE_RIGHT)) {
            throw (new IllegalArgumentException("The side \"" + side + "\" is unknown"));
        }

        // There is no operator left of the first expression or right
        // of the last expression.
        if (side == SIDE_LEFT) {
            expressionsAndOperators.set(expressionIndexToArrayIndex(index) - 1, operator);
        }
        else {
            expressionsAndOperators.set(expressionIndexToArrayIndex(index) + 1, operator);
        }
    }

    /**
     * Add a sub-expression at the end of the expression
     * 
     * @param expression
     * @param operator
     */
    protected void add(Expression expression, String operator) {
        add(size(), expression, operator);
    }

    /**
     * Add a sub-expression at a certain position by the specified index.
     * 
     * @param index
     * @param expression
     * @param operator
     */
    protected void add(int index, Expression expression, String operator) {
        if (expression == null) {
            throw new NullPointerException("Expression cannot be null");
        }

        if (expressionsAndOperators == null) {
            expressionsAndOperators = new ArrayList<Object>();
        }

        if (!expressionsAndOperators.isEmpty()) {
            // We need a valid logicalOperator.
            if (operator == null) {
                throw new NullPointerException("The operator cannot be null");
            }

            // Only binary boolean operators may be used to combine expressions.
            if ((operator.equals(Expression.AND) == false) && (operator.equals(Expression.OR) == false)) {
                throw (new IllegalArgumentException("Operator must be either " + AND + " or " + OR));
            }
        }

        // The logicalOperator will be added first, followed by the expression,
        // resulting in the following:
        //
        // <compound expression> logicalOperator expression
        //
        // However, if index is zero, then the pair go before the first
        // expression. As a result the logicalOperator cannot be first, the
        // given expression must come first. This results in:
        //
        // expression logicalOperator <compound expression>
        //
        if (index <= 0) {
            // Add at beginging
            if (!expressionsAndOperators.isEmpty()) {
                expressionsAndOperators.add(0, operator);
                expressionsAndOperators.add(0, expression);
            } else {
                // Just add expression. There are no other expressions yet.
                expressionsAndOperators.add(expression);
            }
        } else if (index >= size()) {
            // If this the first expression, we'll ignore the logicalOperator.
            // Otherwise we require one.
            if (!expressionsAndOperators.isEmpty()) {
                // Add the logicalOperator and then the expression, resulting
                // in the following:
                //
                // <compound expression> logicalOperator expression
                //
                expressionsAndOperators.add(operator);
                expressionsAndOperators.add(expression);
            } else {
                // Size is zero. We ignore the logicalOperator.
                expressionsAndOperators.add(expression);
            }
        } else {
            // Add someone in between beginning and end. We have the expression
            // index to add at. But we must add the logicalOperator and expression
            // at it's logicalOperator index to displace the logicalOperator and expression.
            expressionsAndOperators.add(expressionIndexToArrayIndex(index) - 1, expression);
            expressionsAndOperators.add(expressionIndexToArrayIndex(index) - 1, operator);
        }
    }

    /**
     * Add a sub-expression at a certain position by the specified index and operator side
     * 
     * @param index
     * @param expression
     * @param operator
     * @param side
     */
    protected void add(int index, Expression expression, String operator, int side) {
        if (side == SIDE_LEFT || index <= 0) {
            add(index, expression, operator);
            return;
        }

        // We must have a valid expression.
        if (expression == null) {
            throw new NullPointerException("Expression cannot be null");
        }

        // We'll only be using the operator if we already have crud.
        if (!expressionsAndOperators.isEmpty()) {
            // We need a valid operator.
            if (operator == null) {
                throw new NullPointerException("The operator cannot be null");
            }

            // Only binary boolean operators may be used to combine expressions.
            if ((operator.equals(Expression.AND) == false) && (operator.equals(Expression.OR) == false)) {
                throw new IllegalArgumentException("Operator must be either " + AND + " or " + OR);
            }
        }

        // inserts <expr> <op> at specified index
        expressionsAndOperators.add(expressionIndexToArrayIndex(index), operator);
        expressionsAndOperators.add(expressionIndexToArrayIndex(index), expression);
    }

    /**
     * Add the compound expression into the expression
     *
     * @param expression
     * @param operator
     */
    protected void addAll(Expression expression, String operator) {
        // We must have a valid expression.
        if (expression == null) {
            throw new NullPointerException("Expression cannot be null");
        }

        if (expression.isCompound() == false) {
            add(expression, operator);
            return;
        }

        // We'll only be using the operator if we already have crud.
        if (!expressionsAndOperators.isEmpty()) {
            // We need a valid operator.
            if (operator == null) {
                throw new NullPointerException("The operator cannot be null");
            }

            // Only binary boolean operators may be used to combine expressions.
            if ((operator.equals(Expression.AND) == false) && (operator.equals(Expression.OR) == false)) {
                throw new IllegalArgumentException("Operator must be either " + Expression.AND + " or " + Expression.OR);
            }
        }

        if (!expressionsAndOperators.isEmpty()) {
            expressionsAndOperators.add(operator);
        }
        expressionsAndOperators.addAll(expression.expressionsAndOperators);
    }

    /**
     * Remove the sub-expression existing at the specified location 
     * 
     * <p>Removing an expression will result in the left-size operator also 
     * being removed. Note that if the first sub-expression is removed, then 
     * the right-side operator will be removed.
     *
     * @param index
     */
    protected void removeSubExpression(int index) {
        // Blowing away the first expression whacks it and the expression
        // to its right. Blowing away any other expression removes it
        // and the operator to its left.
        if (index == 0) {
            if (expressionsAndOperators.size() > 1) {
                expressionsAndOperators.remove(0);
                expressionsAndOperators.remove(0);
            } else {
                // Just remove root expression. There are no others.
                expressionsAndOperators.remove(0);
            }
        } else {
            // Blow away expression and operator to left.
            expressionsAndOperators.remove(expressionIndexToArrayIndex(index));
            expressionsAndOperators.remove(expressionIndexToArrayIndex(index) - 1);
        }
    }


    /**
     * Execute DFS expression traversal to try and extract sub-expression by 
     * subject name
     *
     * @param subject
     * @return
     */
    public Expression find(String subject) {
        if (subject == null) {
            return null;
        }

        if (!isCompound()) {
            if (subject.equals(getSubject().getName())) {
                return this;
            }
        } else {
            for (int i = 0; i < size(); i++) {
                Expression subExpression = getSubExpression(i);
                if (subExpression != null) {
                    Expression matched = subExpression.find(subject);
                    if (matched != null) {
                        return matched;
                    }
                }

            }
        }

        return null;
    }

    /**
     * Complements the current value of the expression
     * 
     * <p>Complementing a negated expression will result in a double negative 
     * which cancels itself out. (e.g. !(!A) = A)
     */
    public Expression complement() {
        complement = !complement;
        return this;
    }

    /**
     * @param distribute
     */
    public Expression complement(boolean distribute) {
        // if not distributing then just change the sign
        if (distribute == false || isCompound() == false) {
            complement();
            return this;
        }

        if (size() == 1) {
            Expression expr = getSubExpression(0);

            if (expr.isCompound() == false) {
                expr = (Expression) expr.clone();
                expr.complement();
            } else {
                expr.complement(distribute);
            }

            return this;
        }

		/*
		 * To generate the compound complement, it is sufficient to take the dual by switching all operators and then
		 * flip the sign for all SimpleExpressions. When applying DeMorgan's Law, however, it is necessary to group
		 * ANDed terms which become OR'd terms.
		 *
		 * !(A * B * C + D) <==> ((!A + !B + !C) * !D)
		 *
		 * The state machine has three cases:
		 *
		 * case 1: Simply add complement of term to parent complement
		 *
		 * (E) (E + + E) + E + ==> (... * !E
		 *
		 * case 2: Need to create a new disjunctive sub-expr and add term
		 *
		 * (E * + E * ==> (... * (E ...
		 *
		 * case 3: ANDed term needs to be added to disjunctive sub-expr
		 *
		 * * E) * E + * E * ==> (... * (... + !E
		 */

        // stores the expression complement at each level of the tree
        Expression logicalComplement = new Expression();

        // group ORs to preserve order of evaluation
        Expression disjunctExpression = new Expression();

        Stack<Serializable> exprStack = new Stack<>(); // store the expression tree
        Stack<Expression> compStack = new Stack<>(); // store the complement tree

        // push initial expression onto the stack
        exprStack.push(this);
        exprStack.push(new Integer(0));

        // push complement structure
        compStack.push(logicalComplement);
        compStack.push(disjunctExpression);

        // walk the parse exprStack
        while(!exprStack.isEmpty()) {
            boolean descend = false;

            // store current expr index
            Integer itr = (Integer) exprStack.pop();

            // process stack expression
            Expression expr = (Expression) exprStack.pop();

            disjunctExpression = (Expression) compStack.pop();
            logicalComplement = (Expression) compStack.pop();

            // break out of loop if descending into child expression, otherwise
            // continue left to right evaluation parse
            for (int i = itr.intValue(); !descend && i < expr.size(); i++) {
                Expression child = expr.getSubExpression(i);
                String op = (i > 0 ? expr.getOperator(i, Expression.SIDE_LEFT) : null);
                String la = (i == expr.size() - 1) ? null : expr.getOperator(i, Expression.SIDE_RIGHT);
                String dual = null;
                if (op != null) {
                    dual = (op.equals(OR) ? AND : OR);
                }

                if (child.isCompound() == false) {
					/*
					 * Simple expression
					 */
                    child = (Expression) child.clone();
                    child.complement();

                    if (op == null || op.equals(Expression.OR)) {
                        if (la == null || la.equals(Expression.OR)) {
                            // case 1
                            logicalComplement.add(child, Expression.AND);
                        } else {
                            // case 2
                            if (disjunctExpression.size() > 0) {
                                disjunctExpression = new Expression();
                            }
                            disjunctExpression.add(child, null);
                            logicalComplement.add(disjunctExpression, Expression.AND);
                        }
                    } else {
                    	// case 3
                        disjunctExpression.add(child, Expression.OR);
                    }
                } else {
                	/*
					 * Compound expression
					 */
                    Expression parent = logicalComplement;
                    logicalComplement = new Expression();

                    // preserve sign of subexpression
                    logicalComplement.setComplement(child.isComplement());

                    if (op == null || op.equals(Expression.OR)) {
                        if (la == null || la.equals(Expression.OR)) {
                            // case 1
                            parent.add(logicalComplement, dual);
                        } else {
                            // case 2
                            if (disjunctExpression.size() > 0) {
                                disjunctExpression = new Expression();
                            }
                            disjunctExpression.add(logicalComplement, null);
                            parent.add(disjunctExpression, Expression.AND);
                        }
                    } else {
                        // case 3
                        disjunctExpression.add(logicalComplement, Expression.OR);
                    }

                    // push parent
                    compStack.push(parent);
                    compStack.push(disjunctExpression);

                    // push child
                    compStack.push(logicalComplement);
                    compStack.push(new Expression());

                    // save parent position
                    exprStack.push(expr);
                    exprStack.push(new Integer(i + 1));

                    // make the child the current expr
                    expr = child;

                    // push child onto minimize stack
                    exprStack.push(child);
                    exprStack.push(new Integer(0));

                    // descend branch to minimize child
                    descend = true;
                }

            }
        }

        this.expressionsAndOperators = logicalComplement.expressionsAndOperators;

        return this;
    }


    /**
     * Descends into the expression to distribute the complement by through 
     * application of DeMorgan's Law.
     * 
     * <p>
     * !(A + B) <==> !A * !B
     * A + !(B + C) <==> A + !B * !C
     * 
     * <p>Sub-optimal solution
     * sub-optimal solution will turn (A + B) into !(!A) + (!B)) if called on 
     * a non complemented compound.
     */
    protected void distributeComplement() {
        // toggle
        this.complement();

        // re-complement with distribution
        this.complement(true);
    }


    /**
     * Reset current state
     * 
     * <p>Sets subject, predicate, value, or any sub-expressions to null and 
     * sets complement to false;
     */
    protected void clear() {
        setSubject(null);
        setPredicate(null);
        setValue(null);
        setComplement(false);
        expressionsAndOperators = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (isCompound() == false) {
            if (isComplement() == true) {
                sb.append("!");
            }
            sb.append("(");
            sb.append("[");
            if (subject != null) {
                sb.append(subject.getName());
                if (subject.getValue() != null) {
                    sb.append(" = ");
                    sb.append(subject.getValue());
                }
            }
            sb.append("] ");

            sb.append(predicate);
            sb.append(" ");

            if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else if (value instanceof Collection) {
				String s = StringUtil.join(",", (Collection<?>) value, v -> {
                    if (v instanceof String) {
                        return(StringUtil.wrap((String) v, "\""));
                    }
                    return v.toString();
                });
				sb.append("[").append(s).append("]");
            } else {
                sb.append(value);
            }
            sb.append(")");
            return sb.toString();
        }
        // Recursive eval of the compound expression.
        toString(this, sb);

        return (sb.toString());
    }

    private void toString(Expression expression, StringBuilder sb) {
        if (expression.isCompound() == false) {
            sb.append(expression.toString());
            return;
        }

        if (expression.isComplement() == true) {
            sb.append("!");
        }

        sb.append("(");

        int size = expression.size();

        for (int i = 0; i < size; i++) {
            Expression subExpression = expression.getSubExpression(i);

            if (i > 0) {
                sb.append(" ");
            }

            if (subExpression.isCompound()) {
                toString(subExpression, sb);
            } else {
                sb.append(subExpression.toString());
            }

            if (i != (size - 1)) {
                sb.append(" ");
                sb.append(expression.getOperator(i, SIDE_RIGHT));
            }
        }
        sb.append(")");
    }

    /**
     * Converts the expression index into the real index of combined 
     * expressions and operators
     * 
     * <p>The expression index is pseudo indexing of sub-expressions contained by the current compound expression.
     * 
     * <p>Expressions are on even numbers starting at zero.
     * 
     * @param index
     * @return
     */
    private int expressionIndexToArrayIndex(int index) {
        return index * 2;
    }

    /**
     * Generate new expression representing minimized (sum-of-products) form 
     * of current expression
     * 
     * <p>Does not modify current expression instance.
     *
     * @return
     */
    public Expression minimized() {
        Expression minimized = ExpressionEngine.minimize(this);
        return minimized;
    }

    /**
     * Perform top-down depth first search of Expression tree
     * 
     * <p>Consume BOTH expression and logical operator nodes to produce binary 
     * pre-fix expression notation wherein the leading operator links the 
     * following sub-expressions. (Only "simple" Expressions will be produced. 
     * Every time an operator is encountered it indicates a new compound 
     * expression was encountered.)
     * 
     * <p>
     * ((A || B)  && C) -> &&, ||, A, B, C
     * (A || (B  && C)) -> || A, &&, B, C
     * (A && B && C)) -> &&, &&, A, B, C
     * 
     * <p>
     * NOTE: postfix will produce "logical" evaluation which gives AND 
     * operations precedence over OR.  As a result, a new expression build 
     * from postfix evaluation will be logically equivalent, but may not be 
     * structurally identical to original expression.
     * 
     * <p>
     * (A && B || C && D)) -> ||, && A, B, &&, C, D
     *
     * @param consumer
     */
    public void prefix(Consumer<Serializable> consumer) {
        if (!this.isCompound()) {
            consumer.accept(this);
        } else {
            // for any compound expression, its postfix representation either treats it as a single minterm (ALL AND clause)
            // (A && B && C) -> &&, &&, A, B, C
            // or the Sum-of-Products form of an OR'd set of 2 or more minterms
            // (A || B && C || D) ->  ||, ||, A, &&, B, C, D

            // calculate min-terms
            List<List<Expression>> minterms = new ArrayList<>();
            List<Expression> minterm = null;
            for (int i = 0; i < this.size(); i++) {
                String op = this.getOperator(i);
                if (op == null || Expression.OR.equals(op)) {
                    minterm = new ArrayList<>();
                    minterms.add(minterm);
                }

                Expression child = this.getSubExpression(i);
                minterm.add(child);
            }

            // Prefix MINTERMS -1 ORs
            for (int i = 0; i < minterms.size() - 1; i++) {
                consumer.accept(Expression.OR);
            }

            for (List<Expression> mt : minterms) {
                // Prefix MINTERM -1 ANDs
                for (int j = 0; j < mt.size() - 1; j++) {
                    consumer.accept(Expression.AND);
                }

                for (Expression e : mt) {
                    if (e.isCompound()) {
                        e.prefix(consumer);
                    } else {
                        consumer.accept(e);
                    }
                }
            }
        }
    }

    /**
     * Determines if two objects are equal
     * 
     * <p>Compound Expressions are considered equal only if they are structurally
     * identical and contain all the same sub-expressions and operators in the same order.
     *
     * @param o
     * @return true if the two objects are identically structured Compound Expressions
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o instanceof Expression == false) {
            return false;
        }

        Expression expression = (Expression) o;

        if (!isCompound()) {
            if (!expression.isCompound()) {
                // For the expressions to be equal, the properties, predicates,
                // expression values, subject values, and complements must also be equal.

                // Subject
                if (getSubject() != null) {
                    if (getSubject().equals(expression.getSubject()) == false) {
                        return false;
                    }
                } else if (expression.getSubject() != null) {
                    return false;
                }

                // Predicate
                if (getPredicate() != null) {
                    if (getPredicate().equals(expression.getPredicate()) == false) {
                        return false;
                    }
                } else if (expression.getPredicate() != null) {
                    return false;
                }

                // Value (check values for "equivalence", we don't care about being same object)
                if (getValue() != null) {
                    if (ReflectUtil2.equivalent(getValue(), expression.getValue()) == false) {
                        return false;
                    }
                } else if (expression.getValue() != null) {
                    return false;
                }

                // Complement
                if (isComplement() != expression.isComplement()) {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        }

        // compare compound
        if (size() != expression.size()) {
            return false;
        }

        // iterate over expressions and operators and evaluate side-by-side comparisons
        for (int i = 0; i < size(); i++) {
            Expression expressionA = getSubExpression(i);
            Expression expressionB = expression.getSubExpression(i);

            if (expressionA != null) {
                if (expressionA.equals(expressionB) == false) {
                    return false;
                }
            } else {
                // both must be null
                if (expressionB != null) {
                    return false;
                }
            }

            String operatorA = getOperator(i, SIDE_RIGHT);
            String operatorB = expression.getOperator(i, SIDE_RIGHT);

            if (operatorA != null) {
                if (operatorA.equals(operatorB) == false) {
                    return false;
                }
            } else {
                if (operatorB != null) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Returns a deep copy of this {@code Expression} instance
     * 
     * <p>Value of a simple expression is passed by reference (NOT cloned).
     *
     * @return  A clone of this <code>Expression</code> instance.
     */
    public Object clone() {
        try {
            Expression cloned = (Expression) (super.clone());

            if (subject != null) {
                cloned.subject = (Subject) subject.clone();
            }

            if (expressionsAndOperators != null) {
                cloned.expressionsAndOperators = new ArrayList<>();
                clone(this, cloned);
            }

            return cloned;
        } catch(CloneNotSupportedException e) {
            throw new InternalError("Cloneable support but cannot clone");
        }
    }

    private void clone(Expression expr, Expression cloned) {
        for (int i = 0; i < expr.expressionsAndOperators.size(); i++) {
            Object o = expr.expressionsAndOperators.get(i);

            if (o instanceof Expression) {
                // clone child expression
                Expression childClone = (Expression) ((Expression) o).clone();

                // add cloned expressions
                cloned.expressionsAndOperators.add(childClone);
            } else {
                // copy operators (Strings)
                cloned.expressionsAndOperators.add(o);
            }
        }
    }
}
