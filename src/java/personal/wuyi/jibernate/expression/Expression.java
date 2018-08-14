package personal.wuyi.jibernate.expression;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

import personal.wuyi.jibernate.util.ReflectUtil2;
import personal.wuyi.jibernate.util.StringUtil;

/**
 * The class for representing query expression.
 * 
 * @author  Wuyi Chen
 * @date    08/12/2018
 * @version 1.0
 * @since   1.0
 */
public class Expression implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;

    // For combining 2 expressions.
    public static final String AND                = "&&";
    public static final String OR                 = "||";
    
    // For operators in a single expression.
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

    // If the expression is a single expression, those 3 fields will be populated.
    private Subject subject  = null;
    private String  operator = null;
    private Object  value    = null;

    // If the expression is a compound expression, this list will be populated.
    /** The list of sub-expressions and operators */
    private List<Object> subExpressionAndOperatorList = null;

    /**
     * Constructs a {@code Expression}.
     * 
     * @since   1.0
     */
    protected Expression() { }

    /**
     * Constructs a {@code Expression}.
     * 
     * <p>This method will create a new simple expression.
     *
     * @param  subject
     *         The field name in the Java class (not the column name in 
     *         database).
     *         
     * @param  operator
     *         The operator for the expression.
     * 
     * @param  value
     *         The value for the expression.
     *         
     * @since   1.0
     */
    public Expression(String subject, String operator, Object value) {
        this(new Subject(subject, null), operator, value);
    }

    /**
     * Constructs a {@code Expression}.
     * 
     * <p>This method will create a new simple expression.
     * 
     * @param  subject
     *         The field name in the Java class (not the column name in 
     *         database).
     *         
     * @param  operator
     *         The operator for the expression.
     *         
     * @param  value
     *         The value for the expression.
     *         
     * @since   1.0
     */
    public Expression(Subject subject, String operator, Object value) {
        setSubject(subject);
        setOperator(operator);
        setValue(value);
    }

    /**
     * Constructs a {@code Expression}.
     * 
     * <p>Create a new compound expression with the argument sub-expression.
     *
     * @param  expression
     *         The expression needs to be compounded.
     *         
     * @since   1.0
     */
    public Expression(Expression expression) {
    	addSubExpressionWithOperator(expression, null);
    }
    
    public Subject getSubject()                      { return subject;               }
    public void    setSubject(Subject subject)       { this.subject = subject;       }
    public String  getOperator()                     { return operator;              }
    public void    setOperator(String operator)      { this.operator = operator;     }
    public Object  getValue()                        { return value;                 }
    public void    setValue(Object value)            { this.value = value;           }
    public boolean isComplement()                    { return complement;            }
    public void    setComplement(boolean complement) { this.complement = complement; }

    /**
     * Check an expression is a simple statement or is compound.
     * 
     * If an expression has sub-expression(s), it is a compound expression.
     *
     * @return  {@code true} if it is a compound expression;
     *          {@code false} otherwise.
     *          
     * @since   1.0
     */
    public boolean isCompound() {
        if (subExpressionAndOperatorList == null || subExpressionAndOperatorList.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Get the number of the sub-expressions of an expression.
     * 
     * <p>If an expression is a simple expression, the number of 
     * sub-expressions will be 0. 
     * 
     * @since   1.0
     */
    public int getNumberOfSubExpression() {
        if (subExpressionAndOperatorList == null || subExpressionAndOperatorList.size() == 0) {
            return 0;
        } else {
        	    // the list will look like  [Ex, Op, Ex, Op, Ex, Op, Ex]
        	    // ignore the last Ex, the list will be (Ex, Op) pairs, so divided by 2.
        	    // and then add the Ex back.
            return ((subExpressionAndOperatorList.size() - 1) / 2) + 1;
        }
    }
    
    /**
     * Operate AND with an expression.
     *
     * @param  subject
     *         The field name in the Java class (not the column name in 
     *         database).
     *         
     * @param  operator
     *         The operator for the expression.
     * 
     * @param  value
     *         The value for the expression.
     *         
     * @return  The new compound expression after doing AND operation on 
     *          another expression.
     *          
     * @since   1.0
     */
    public Expression and(String subject, String operator, Object value) {
        return and(new Expression(subject, operator, value));
    }

    /**
     * Operate AND with an expression.
     *
     * @param  expression
     *         Another expression needs to do AND operation with this expression.
     * 
     * @return  The new compound expression after doing AND operation on 
     *          another expression.
     * 
     * @since   1.0
     */
    public Expression and(Expression expression) {
    		return combineExpression(AND, expression);
    }

    /**
     * Operate AND on multiple expressions
     *
     * @param  subExpressions
     *         One or more expressions need to do AND operation.
     * 
     * @return  The new compound expression.
     * 
     * @since   1.0
     */
    public static Expression and(Expression... subExpressions) {        
        return combineExpressions(AND, subExpressions);
    }
    
    /**
     * Operate OR with an expression.
     *
     * @param  subject
     *         The field name in the Java class (not the column name in 
     *         database).
     *         
     * @param  operator
     *         The operator for the expression.
     * 
     * @param  value
     *         The value for the expression.
     *         
     * @return  The new compound expression after doing OR operation on 
     *          another expression.
     *          
     * @since   1.0
     */
    public Expression or(String subject, String predicate, Object value) {
        return or(new Expression(subject, predicate, value));
    }

    /**
     * Operate OR with an expression.
     *
     * @param  expression
     *         Another expression needs to do OR operation with this expression.
     * 
     * @return  The new compound expression after doing OR operation on 
     *          another expression.
     * 
     * @since   1.0
     */
    public Expression or(Expression expression) {
        return combineExpression(OR, expression);
    }

    /**
     * Operate OR on multiple expressions
     *
     * @param  subExpressions
     *         One or more expressions need to do OR operation.
     * 
     * @return  The new compound expression.
     * 
     * @since   1.0
     */
    public static Expression or(Expression... subExpressions) {
        return combineExpressions(OR, subExpressions);
    }
    
    /**
     * Combine this expression with another expression.
     * 
     * @param  operator
     *         The operator of the combination.
     *         
     * @param  expression
     *         Another expression needs to combine with this expression.
     *         
     * @return  The new compound expression.
     * 
     * @since   1.0
     */
    public Expression combineExpression(String operator, Expression expression) {
       	// If current expression is a simple expression, it will compound 
	    // itself prior to doing AND/OR operation with another expression.
    		if (!isCompound()) {
            compound();
        }
    	addSubExpressionWithOperator(expression, operator);
        return this;
    }
    
    /**
     * Combine multiple expressions.
     * 
     * @param  operator
     *         The operator of the combination.
     *         
     * @param  subExpressions
     *         One or more expressions need to be combined.
     * 
     * @return  The new compound expression.
     * 
     * @since   1.0
     */
    public static Expression combineExpressions(String operator, Expression... subExpressions) {
    	final Expression expression = new Expression();

        for (int i = 0; i < subExpressions.length; i++) {
            expression.addSubExpressionWithOperator(subExpressions[i], operator);
        }

        return expression;
    }


    /**
     * Compound the current expression.
     * 
     * <p>This method will make the current expression as sub-expression so 
     * that it is ready for combining with other expressions.
     *
     * @return  The compounded version of the current expression.
     * 
     * @since   1.0
     */
    private Expression compound() {
        if (subject != null || operator != null || value != null || subExpressionAndOperatorList != null) {
            final Expression subExpr = (Expression) this.clone();
            reset();
            addSubExpressionWithOperator(subExpr, null);
        }

        return this;
    }


    /**
     * Get the sub-expression by index.
     *
     * <p>Expressions are on even numbers starting at 0.
     *
     * @param  index
     *         The expression index of the sub-expression.
     *         
     * @return  The target sub-expression.
     * 
     * @since   1.0
     */
    public Expression getSubExpression(int index) {
        return (Expression) subExpressionAndOperatorList.get(expressionIndexToArrayIndex(index));
    }
    
    /**
     * Set an expression into the list of sub-expressions.
     *
     * @param  index
     *         The expression index.
     * 
     * @param  expression
     *         The new expression needs to be added.
     *         
     * @since   1.0
     */
    protected void setSubExpression(int index, Expression expression) {
        subExpressionAndOperatorList.set(expressionIndexToArrayIndex(index), expression);
    }

    /**
     * Get the operator on the left side of a sub-expression from the list of 
     * sub-expressions..
     * 
     * <p>The operator is referenced by the specified expression index if 
     * operator exists to the immediate left of the expression.
     *
     * @param  index
     *         The expression index of the sub-expression.
     *         
     * @return  The operator on the left side of the specified sub-expression.
     * 
     * @since   1.0
     */
    public String getOperator(int index) {
        return getOperator(index, SIDE_LEFT);
    }

    /**
     * Get the operator of one side of a sub-expression from the list of 
     * sub-expressions.
     * 
     * <p>The operator referenced by the specified expression index where side 
     * specifies the operator existing to the immediate left or right of the 
     * expression.
     *
     * @param  index
     *         The expression index of the sub-expression.
     *         
     * @param  side
     *         Which side of the expression, left or right.
     *         
     * @return  The operator on one side of the specified sub-expression.
     * 
     * @since   1.0
     */
    protected String getOperator(int index, int side) {
        if ((side != SIDE_LEFT) && (side != SIDE_RIGHT)) {
            throw new IllegalArgumentException("The side \"" + side + "\" is unknown");
        }

        String operator = null;

        if (side == SIDE_LEFT) {
            int realIndex = expressionIndexToArrayIndex(index) - 1;
            // There is no operator left of the first expression
            if (realIndex > 0) {
                operator = (String) subExpressionAndOperatorList.get(realIndex);
            }
        } else {
            int realIndex = expressionIndexToArrayIndex(index) + 1;
            // There is no operator right of the last expression.
            if (realIndex < subExpressionAndOperatorList.size()) {
                operator = (String) subExpressionAndOperatorList.get(realIndex);
            }
        }

        return operator;
    }
    
    /**
     * Set an operator into the list of sub-expressions.
     *
     * @param  index
     *         The expression index of the referenced sub-expression.
     *         
     * @param  side
     *         Which side of the expression, left or right.
     *         
     * @param  operator
     *         The operator needs to be added.
     *         
     * @since   1.0
     */
    protected void setOperator(int index, int side, String operator) {
        if ((side != SIDE_LEFT) || (side != SIDE_RIGHT)) {
            throw (new IllegalArgumentException("The side \"" + side + "\" is unknown"));
        }

        if (side == SIDE_LEFT) {
            subExpressionAndOperatorList.set(expressionIndexToArrayIndex(index) - 1, operator);
        } else {
            subExpressionAndOperatorList.set(expressionIndexToArrayIndex(index) + 1, operator);
        }
    }

    /**
     * Add a sub-expression at the end of the list of sub-expressions.
     * 
     * @param  expression
     *         The expression needs to be added.
     *         
     * @param  operator
     *         The operator needs to be added.
     *         
     * @since   1.0
     */
    protected void addSubExpressionWithOperator(Expression expression, String operator) {
    	    addSubExpressionWithOperator(getNumberOfSubExpression(), expression, operator);
    }

    /**
     * Add a sub-expression with a operator into a list of sub-expressions.
     * 
     * <p>There are 2 situations for adding an expression with an operator:
     * <ul>
     *   <li>If the index > 0, add the operator first, and then add the sub-expression.
     *     <pre>
     *       [sub-expression list] + operator + sub-expression
     *     </pre>
     *   <li>If the index = 0, add the sub-expression first, and then add the operator.
     *     <pre>
     *       sub-expression + operator + [sub-expression list]
     *     </pre>
     * </ul>
     * 
     * This method will add the operator first, and then add the sub-expression to the list of sub-expressions.
     * 
     * @param  index
     *         The expression index.
     *         
     * @param  expression
     *         The expression needs to be added.
     * 
     * @param  operator
     *         The operator needs to be added.
     *         
     * @since   1.0     
     */
    protected void addSubExpressionWithOperator(int index, Expression expression, String operator) {
        if (expression == null) {
            throw new NullPointerException("Expression cannot be null");
        }

        if (subExpressionAndOperatorList == null) {
            subExpressionAndOperatorList = new ArrayList<>();
        }

        if (!subExpressionAndOperatorList.isEmpty()) {
            if (operator == null) {
                throw new NullPointerException("The operator cannot be null");
            }

            // Only binary boolean operators may be used to combine expressions.
            if (!operator.equals(Expression.AND) && !operator.equals(Expression.OR)) {
                throw new IllegalArgumentException("Operator must be either " + AND + " or " + OR);
            }
        }
        
        if (subExpressionAndOperatorList.isEmpty()) {            // if the sub-expression list is empty, just add the new sub-expression.
        	subExpressionAndOperatorList.add(expression);
        } else {
        	if (index <= 0) {                                    // add at the left end
        		subExpressionAndOperatorList.add(0, operator);
        		subExpressionAndOperatorList.add(0, expression);
            } else if (index >= getNumberOfSubExpression()) {    // add at the right end
                subExpressionAndOperatorList.add(operator);
                subExpressionAndOperatorList.add(expression);
            } else {                                             // add at the middle
                subExpressionAndOperatorList.add(expressionIndexToArrayIndex(index) - 1, expression);
                subExpressionAndOperatorList.add(expressionIndexToArrayIndex(index) - 1, operator);
            }
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
        	addSubExpressionWithOperator(index, expression, operator);
            return;
        }

        // We must have a valid expression.
        if (expression == null) {
            throw new NullPointerException("Expression cannot be null");
        }

        // We'll only be using the operator if we already have crud.
        if (!subExpressionAndOperatorList.isEmpty()) {
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
        subExpressionAndOperatorList.add(expressionIndexToArrayIndex(index), operator);
        subExpressionAndOperatorList.add(expressionIndexToArrayIndex(index), expression);
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
        	addSubExpressionWithOperator(expression, operator);
            return;
        }

        // We'll only be using the operator if we already have crud.
        if (!subExpressionAndOperatorList.isEmpty()) {
            // We need a valid operator.
            if (operator == null) {
                throw new NullPointerException("The operator cannot be null");
            }

            // Only binary boolean operators may be used to combine expressions.
            if ((operator.equals(Expression.AND) == false) && (operator.equals(Expression.OR) == false)) {
                throw new IllegalArgumentException("Operator must be either " + Expression.AND + " or " + Expression.OR);
            }
        }

        if (!subExpressionAndOperatorList.isEmpty()) {
            subExpressionAndOperatorList.add(operator);
        }
        subExpressionAndOperatorList.addAll(expression.subExpressionAndOperatorList);
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
            if (subExpressionAndOperatorList.size() > 1) {
                subExpressionAndOperatorList.remove(0);
                subExpressionAndOperatorList.remove(0);
            } else {
                // Just remove root expression. There are no others.
                subExpressionAndOperatorList.remove(0);
            }
        } else {
            // Blow away expression and operator to left.
            subExpressionAndOperatorList.remove(expressionIndexToArrayIndex(index));
            subExpressionAndOperatorList.remove(expressionIndexToArrayIndex(index) - 1);
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
            for (int i = 0; i < getNumberOfSubExpression(); i++) {
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

        if (getNumberOfSubExpression() == 1) {
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
            for (int i = itr.intValue(); !descend && i < expr.getNumberOfSubExpression(); i++) {
                Expression child = expr.getSubExpression(i);
                String op = (i > 0 ? expr.getOperator(i, Expression.SIDE_LEFT) : null);
                String la = (i == expr.getNumberOfSubExpression() - 1) ? null : expr.getOperator(i, Expression.SIDE_RIGHT);
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
                            logicalComplement.addSubExpressionWithOperator(child, Expression.AND);
                        } else {
                            // case 2
                            if (disjunctExpression.getNumberOfSubExpression() > 0) {
                                disjunctExpression = new Expression();
                            }
                            disjunctExpression.addSubExpressionWithOperator(child, null);
                            logicalComplement.addSubExpressionWithOperator(disjunctExpression, Expression.AND);
                        }
                    } else {
                    	// case 3
                        disjunctExpression.addSubExpressionWithOperator(child, Expression.OR);
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
                            parent.addSubExpressionWithOperator(logicalComplement, dual);
                        } else {
                            // case 2
                            if (disjunctExpression.getNumberOfSubExpression() > 0) {
                                disjunctExpression = new Expression();
                            }
                            disjunctExpression.addSubExpressionWithOperator(logicalComplement, null);
                            parent.addSubExpressionWithOperator(disjunctExpression, Expression.AND);
                        }
                    } else {
                        // case 3
                        disjunctExpression.addSubExpressionWithOperator(logicalComplement, Expression.OR);
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

        this.subExpressionAndOperatorList = logicalComplement.subExpressionAndOperatorList;

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
     * Reset the state of the current expression.
     * 
     * <p>Sets subject, predicate, value, or any sub-expressions to null and 
     * sets complement to false;
     */
    protected void reset() {
        setSubject(null);
        setOperator(null);
        setValue(null);
        setComplement(false);
        subExpressionAndOperatorList = null;
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

            sb.append(operator);
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

        int size = expression.getNumberOfSubExpression();

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
            for (int i = 0; i < this.getNumberOfSubExpression(); i++) {
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
                if (getOperator() != null) {
                    if (getOperator().equals(expression.getOperator()) == false) {
                        return false;
                    }
                } else if (expression.getOperator() != null) {
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
        if (getNumberOfSubExpression() != expression.getNumberOfSubExpression()) {
            return false;
        }

        // iterate over expressions and operators and evaluate side-by-side comparisons
        for (int i = 0; i < getNumberOfSubExpression(); i++) {
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

            if (subExpressionAndOperatorList != null) {
                cloned.subExpressionAndOperatorList = new ArrayList<>();
                clone(this, cloned);
            }

            return cloned;
        } catch(CloneNotSupportedException e) {
            throw new InternalError("Cloneable support but cannot clone");
        }
    }

    private void clone(Expression expr, Expression cloned) {
        for (int i = 0; i < expr.subExpressionAndOperatorList.size(); i++) {
            Object o = expr.subExpressionAndOperatorList.get(i);

            if (o instanceof Expression) {
                // clone child expression
                Expression childClone = (Expression) ((Expression) o).clone();

                // add cloned expressions
                cloned.subExpressionAndOperatorList.add(childClone);
            } else {
                // copy operators (Strings)
                cloned.subExpressionAndOperatorList.add(o);
            }
        }
    }
}
