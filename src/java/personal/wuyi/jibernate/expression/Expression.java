package personal.wuyi.jibernate.expression;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;

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
    	combineExpression(null, expression);
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
        if (subExpressionAndOperatorList == null) {
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
	 * <p>If current expression is a simple expression, it will compound 
	 * itself prior to doing AND/OR operation with another expression.
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
            subExpressionAndOperatorList = new ArrayList<>();
            subExpressionAndOperatorList.add(subExpr);
        } else {
        	subExpressionAndOperatorList = new ArrayList<>();
        }

        return this;
    }

    /**
     * Get the sub-expression by index.
     *
     * <p>Expressions are on even numbers starting at 0.
     * 
     * <p>For a simple expression, this method will return {@code null}.
     *
     * @param  index
     *         The expression index of the sub-expression.
     *         
     * @return  The target sub-expression.
     * 
     * @since   1.0
     */
    public Expression getSubExpression(int index) {
    	if (!isCompound()) {
    		return null;
    	} else {
    		checkExpressionIndexOutOfBound(index);
    		return (Expression) subExpressionAndOperatorList.get(convertExpressionIndexToArrayIndex(index));
    	}
    }
    
    /**
     * Check the expression index is out of bound or not.
     * 
     * @param  index
     *         The expression index needs to be checked.
     *         
     * @since   1.0
     */
    private void checkExpressionIndexOutOfBound(int index) {
    	int size = getNumberOfSubExpression();
		if (index < 0 || index > size - 1) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}
    }
    
    /**
     * Set an expression into the list of sub-expressions.
     * 
     * <p>For a simple expression, this method will do nothing.
     * 
     * <p>This method could only update the existing sub-expressions (because 
     * of new sub-expression can be appended into an expression only with 
     * operator).
     *
     * @param  index
     *         The expression index.
     * 
     * @param  expression
     *         The new expression needs to be added.
     *         
     * @since   1.0
     */
    protected void replaceSubExpression(int index, Expression expression) {
    	if (isCompound()) {
    		Preconditions.checkArgument(index >= 0,                         "index can not be negative.");
    		Preconditions.checkArgument(index < getNumberOfSubExpression(), "this method can only replace an existing sub-expression.");
    		subExpressionAndOperatorList.set(convertExpressionIndexToArrayIndex(index), expression);
    	}
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
     * <p>If an expression is simple, this method will return {@code null}.
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
    	if (!isCompound()) {
    		return null;
    	}
    	
        if ((side != SIDE_LEFT) && (side != SIDE_RIGHT)) {
            throw new IllegalArgumentException("The side \"" + side + "\" is unknown");
        }
        checkExpressionIndexOutOfBound(index);

        String operator = null;

        if (side == SIDE_LEFT) {
            int realIndex = convertExpressionIndexToArrayIndex(index) - 1;
            // There is no operator left of the first expression
            if (realIndex > 0) {
                operator = (String) subExpressionAndOperatorList.get(realIndex);
            }
        } else {
            int realIndex = convertExpressionIndexToArrayIndex(index) + 1;
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
        if ((side != SIDE_LEFT) && (side != SIDE_RIGHT)) {
            throw new IllegalArgumentException("The side \"" + side + "\" is unknown");
        }
        checkExpressionIndexOutOfBound(index);

        if (side == SIDE_LEFT) {
        	if (index == 0) {
        		throw new IllegalArgumentException("Can not set an operator on the left side of the first sub-expression without an expression");
        	}
            subExpressionAndOperatorList.set(convertExpressionIndexToArrayIndex(index) - 1, operator);
        } else {
        	if (index == getNumberOfSubExpression() - 1) {
        		throw new IllegalArgumentException("Can not set an operator on the right side of the last sub-expression without an expression");
        	}
            subExpressionAndOperatorList.set(convertExpressionIndexToArrayIndex(index) + 1, operator);
        }
    }

    /**
     * Add a sub-expression at the end of the list of sub-expressions.
     * 
     * <p>This method is not valid for any simple expression. The simple 
     * expression needs to be compounded before adding a new sub-expression.
     * You can use 
     * {@code combineExpression(String operator, Expression expression)} 
     * for a simple expression.
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
     * <p>This method is not valid for any simple expression. The simple 
     * expression needs to be compounded before adding a new sub-expression.
     * You can use 
     * {@code combineExpression(String operator, Expression expression)} 
     * for a simple expression.
     * 
     * <p>There are 2 situations for adding an expression with an operator:
     * <ul>
     *   <li>If the index > 0, add the operator first, and then add the 
     *   sub-expression.
     *     <pre>
     *       [sub-expression list] + operator + sub-expression
     *     </pre>
     *   <li>If the index = 0, add the sub-expression first, and then add the 
     *   operator.
     *     <pre>
     *       sub-expression + operator + [sub-expression list]
     *     </pre>
     * </ul>
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
    	validateConditionBeforeAddingSubExpression(expression, operator);
        
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
                subExpressionAndOperatorList.add(convertExpressionIndexToArrayIndex(index) - 1, expression);
                subExpressionAndOperatorList.add(convertExpressionIndexToArrayIndex(index) - 1, operator);
            }
        } 
    }

    /**
     * Add a sub-expression with a operator on certain side into a list of 
     * sub-expressions.
     * 
     * <p>This method is not valid for any simple expression. The simple 
     * expression needs to be compounded before adding a new sub-expression.
     * You can use 
     * {@code combineExpression(String operator, Expression expression)} 
     * for a simple expression.
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
     * @param  side
     *         The operator will be added to which side of the new 
     *         sub-expression, left or right.
     */
    protected void addSubExpressionWithOperator(int index, Expression expression, String operator, int side) {
        if (side == SIDE_LEFT || index <= 0) {
        	addSubExpressionWithOperator(index, expression, operator);
            return;
        }

        validateConditionBeforeAddingSubExpression(expression, operator);

        // inserts <expr> <op> at specified index
        subExpressionAndOperatorList.add(convertExpressionIndexToArrayIndex(index), operator);
        subExpressionAndOperatorList.add(convertExpressionIndexToArrayIndex(index), expression);
    }

	/**
	 * Combine this expression with another compound expression.
	 * 
	 * <p>If current expression is a simple expression, it will compound 
	 * itself prior to doing AND/OR operation with another expression.
	 * 
	 * @param  operator
	 *         The operator of the combination.
	 *         
	 * @param  expression
	 *         Another compound expression needs to combine with this 
	 *         expression.
	 *         
	 * @return  The new compound expression.
	 * 
	 * @since   1.0
	 */
    public void combineCompoundExpression(String operator, Expression expression) {
    	// If current expression is a simple expression, it will compound 
	    // itself prior to doing AND/OR operation with another expression.
    	if (!isCompound()) {
            compound();
        }
    	addCompoundExpression(expression, operator);
    }

    /**
     * Add a compound expression into the expression.
     * 
     * <p>This method also allow to insert a simple expression.
     * 
     * <p>This method is not valid for any simple expression. The simple 
     * expression needs to be compounded before adding a new sub-expression.
     * You can use 
     * {@code combineCompoundExpression(String operator, Expression expression)} 
     * for a simple expression.
     *
     * @param  expression
     *         The compound expression needs to be added.
     *         
     * @param  operator
     *         The operator needs to be added.
     *         
     * @since   1.0  
     */
    protected void addCompoundExpression(Expression expression, String operator) {
        if (expression == null) {
            throw new NullPointerException("Expression cannot be null");
        }

        if (!expression.isCompound()) {
        	addSubExpressionWithOperator(expression, operator);
            return;
        }

        validateConditionBeforeAddingSubExpression(expression, operator);

        if (!subExpressionAndOperatorList.isEmpty()) {
            subExpressionAndOperatorList.add(operator);
        }
        subExpressionAndOperatorList.addAll(expression.subExpressionAndOperatorList);
    }
    
    /**
     * Validate the conditions before adding a new sub-expression into the 
     * sub-expression list.
     * 
     * <p>Several conditions will be validated:
     * <ul>
     *   <li>The new sub-expression can not be {@code null}.
     *   <li>The expression itself can not be simple expression (needs to be compounded before adding a new sub-expression).
     *   <li>If the sub-expression list is not empty, the operator is 
     *       mandatory (can not be {@code null}) and can only be AND or OR.
     * </ul>
     * 
     * @param  expression
     *         The expression needs to be added.
     *         
     * @param  operator
     *         The operator needs to be added.
     *         
     * @since   1.0 
     */
    protected void validateConditionBeforeAddingSubExpression(Expression expression, String operator) {
    	if (expression == null) {
            throw new NullPointerException("Expression cannot be null");
        }
        
        if (!isCompound()) {
            throw new IllegalArgumentException("This method is not valid for any simple expression. "
            		+ "A simple expression needs to be compounded before adding a new sub-expression.");
        }

        if (!subExpressionAndOperatorList.isEmpty()) {
            if (operator == null) {
                throw new NullPointerException("The operator cannot be null");
            }

            if (!operator.equals(Expression.AND) && !operator.equals(Expression.OR)) {
                throw new IllegalArgumentException("Operator must be either " + AND + " or " + OR);
            }
        }
    }

    /**
     * Remove the sub-expression from the list of sub-expressions. 
     * 
     * <p>If the current expression is a simple expression, so there is no 
     * sub-expression, so this method will do nothing.
     * 
     * <p>If the current expression is a compound expression, so removing an 
     * expression will result in the left-size operator also being removed. 
     * Note that if the first sub-expression is removed, then the right-side 
     * operator will be removed.
     * 
     * <p>If there is only one sub-expression left after removing, this 
     * current expression will be simplified by the single sub-expression.
     *
     * @param  index
     *         The expression index indicates which sub-expression needs to be 
     *         removed.
     *         
     * @since   1.0 
     */
    protected void removeSubExpression(int index) {
    	if (!isCompound()) {
    		return;
    	} else {
    		checkExpressionIndexOutOfBound(index);
    		
    		if (index == 0) {
    			subExpressionAndOperatorList.remove(0);
    	        subExpressionAndOperatorList.remove(0);
    	    } else {
    	        subExpressionAndOperatorList.remove(convertExpressionIndexToArrayIndex(index));
    	        subExpressionAndOperatorList.remove(convertExpressionIndexToArrayIndex(index) - 1);
    	    }
    		
    		// if only one sub-expression left, simplify to a simple expression
    		if (getNumberOfSubExpression() == 1) {
    			simplify(getSubExpression(0));
    		}
    	
    	}
    }
    
    /**
     * Simplify the current expression.
     * 
     * <p>If there is only one sub-expression left after removing, this 
     * current expression needs to be simplified by the unique sub-expression.
     * 
     * @param  subExpression
     *         The sub-expression for simplifying the current expression.
     *         
     * @since   1.0 
     */
    private void simplify(Expression subExpression) {
    	this.setSubject(subExpression.getSubject());
    	this.setOperator(subExpression.getOperator());
    	this.setValue(subExpression.getValue());
    	this.setComplement(subExpression.isComplement());
    	
    	subExpressionAndOperatorList = null;
    }

    /**
     * Find the sub-expression by subject.
     * 
     * <p>Because of expressions can be nested among multiple level, so this 
     * method uses DFS to find the matched expression recursively.
     *
     * @param  subject
     *         The subject for matching.
     *         
     * @return  The matched sub-expression.
     * 
     * @since   1.0 
     */
    public Expression findSubExpression(String subject) {
        if (subject == null) {
            return null;
        }

        if (!isCompound()) {
            if (subject.equals(getSubject().getName())) {
                return this;
            }
        } else {
            for (int i = 0; i < getNumberOfSubExpression(); i++) {
                final Expression subExpression = getSubExpression(i);
                if (subExpression != null) {
                    final Expression matched = subExpression.findSubExpression(subject);
                    if (matched != null) {
                        return matched;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Complements this expression.
     * 
     * <p>Complementing is to do NOT operation on a boolean value.
     * 
     * <p>Complementing a negative expression will result in a double negative 
     * which cancels itself out. (e.g. !(!A) = A)
     * 
     * @since   1.0 
     */
    public Expression complement() {
        complement = !complement;
        return this;
    }
    
    /**
     * Complements this expression.
     * 
     * <p>Complementing is to do NOT operation on a boolean value.
     * 
     * <p>Complementing a negative expression will result in a double negative 
     * which cancels itself out. (e.g. !(!A) = A)
     * 
     * @param  expression
     *         The expression needs to get the complement.
     *         
     * @since   1.0 
     */
    public Expression complement(Expression expression) {
    	expression = (Expression) expression.clone();
    	return expression.complement();
    }

    /**
     * Complements the current value of the expression with distribute.
     * 
     * @param  distribute
     *         The distribute option.
     * 
     * @since   1.0 
     */
    public Expression complement(boolean distribute) {
        // if not distributing then just change the sign
        if (distribute == false || isCompound() == false) {
            complement();
            return this;
        }

        if (getNumberOfSubExpression() == 1) {
            Expression expr = getSubExpression(0);

            if (expr.isCompound()) {
            	expr.complement(distribute);
            } else {
            	complement(expr);
            }

            return this;
        }

        Stack<Serializable> exprStack      = new Stack<>();    // store the expression tree
        exprStack.push(this);                                  // push the initial expression onto the stack
        exprStack.push(new Integer(0));                        // push the expression index of the initial expression into the stack
        
        Stack<Expression>   compStack      = new Stack<>();    // store the complement tree
        Expression          complementExpr = new Expression(); // stores the expression complement at each level of the tree
        Expression          disjunctExpr   = new Expression(); // group all the ORs together
        compStack.push(complementExpr);
        compStack.push(disjunctExpr);

        while(!exprStack.isEmpty()) {
            boolean descend = false;

            Integer    exprIndex = (Integer)    exprStack.pop();   // get the expression index of the current expression
            Expression expr      = (Expression) exprStack.pop();   // get the current expression

            disjunctExpr    = (Expression) compStack.pop();
            complementExpr  = (Expression) compStack.pop();

            // break out of loop if descending into child expression, otherwise
            // continue left to right evaluation parse
            for (int i = exprIndex.intValue(); !descend && i < expr.getNumberOfSubExpression(); i++) {
                Expression subExpr   = expr.getSubExpression(i);
                String     leftOptr  = i > 0                                   ? expr.getOperator(i, Expression.SIDE_LEFT)  : null; // the left operator
                String     rightOptr = i < expr.getNumberOfSubExpression() - 1 ? expr.getOperator(i, Expression.SIDE_RIGHT) : null; // the right operator

                if (!subExpr.isCompound()) {                // simple expression
                	complement(subExpr);
                	applyDeMorganLaw(complementExpr, disjunctExpr, subExpr, leftOptr, rightOptr); 
                } else {                                    // compound expression
                    Expression parentExpr = complementExpr;                 // use parent expression to preserve the state of the complement expression
                    complementExpr = new Expression();                       
                    complementExpr.setComplement(subExpr.isComplement());   // use complement expression to preserve sign of the sub-expression

                    applyDeMorganLaw(parentExpr, disjunctExpr, complementExpr, leftOptr, rightOptr);

                    // push parent
                    compStack.push(parentExpr);
                    compStack.push(disjunctExpr);

                    // push child
                    compStack.push(complementExpr);
                    compStack.push(new Expression());

                    // save parent position
                    exprStack.push(expr);
                    exprStack.push(new Integer(i + 1));

                    // make the child the current expression
                    expr = subExpr;

                    // push child onto minimize stack
                    exprStack.push(subExpr);
                    exprStack.push(new Integer(0));

                    // descend branch to minimize child
                    descend = true;
                }

            }
        }

        this.subExpressionAndOperatorList = complementExpr.subExpressionAndOperatorList;

        return this;
    }
    
    /**
     * Apply DeMorgan's Law to a sub-expression.
     * 
     * <p>The DeMorgan's Law is (* is AND, + is OR):
     * <ul>
     *   <li>!(P * Q) <==> !P + !Q
     *   <li>!(P + Q) <==> !P * !Q
     * </ul>
     * 
     * <p>When applying DeMorgan's Law, it is necessary to group ANDed terms which become OR'd terms, like
     * <pre>
     *     !(A * B * C + D) <==> ((!A + !B + !C) * !D)
     * </pre>
     * <p>So {@code disjunctExpr} is to group !A, !B, !C together
     * 
     * <p>The state machine has three cases when processing each sub-expression:
     * <ul>
     *   <li>Case 1: Left operator is OR, right operator is OR, so
     *     <pre>
     *       if 
     *          [original expression] + E + 
     *       do 
     *          [complement expression] * !E
     *     </pre>
     *   <li>Case 2: Left operator is OR, right operator is AND, so add the 
     *   flipped sub-expression into disjunct expression and then add the 
     *   disjunct expression into the complement expression.
     *     <pre>
     *       if 
     *          [original expression] + E *
     *       do 
     *          [disjunct expression] <= E
     *          [original expression] * [disjunct expression]
     *     </pre>
     *   <li>Case 3: Left operator is AND 
     *   (whatever the right operator is AND or OR), 
     *   Don't add the flipped sub-expression to the complement expression directly. Cache it to disjunct expression.
     *     <pre>
     *       if 
     *          [original expression] * E 
     *       do 
     *          [disjunct expression] + !E
     *     </pre>
     *   <li>
     * </ul>
     * 
     * @param  complementExpr
     *         The complement expression of the original expression.
     *         
     * @param  disjunctExpr
     *         The disjunct expression to group ORs to preserve order of 
     *         evaluation.
     * 
     * @param  subExpr
     *         The sub-expression from the original expression.
     * 
     * @param  leftOptr
     *         The left operator of the sub-expression.
     * 
     * @param  rightOptr
     *         The right operator of the sub-expression.
     *         
     * @since   1.0      
     */
    public void applyDeMorganLaw(Expression complementExpr, Expression disjunctExpr, Expression subExpr, String leftOptr, String rightOptr) {
    	if (leftOptr == null || leftOptr.equals(Expression.OR)) {
            if (rightOptr == null || rightOptr.equals(Expression.OR)) {
                // case 1
                complementExpr.addSubExpressionWithOperator(subExpr, Expression.AND);
            } else {
                // case 2
                if (disjunctExpr.getNumberOfSubExpression() > 0) {
                    disjunctExpr = new Expression();
                }
                disjunctExpr.addSubExpressionWithOperator(subExpr, null);
                complementExpr.addSubExpressionWithOperator(disjunctExpr, Expression.AND);
            }
        } else {
        	// case 3
            disjunctExpr.addSubExpressionWithOperator(subExpr, Expression.OR);
        }
    }

    /**
     * Reset the state of the current expression.
     * 
     * <p>This method will set subject, operator, value, or any 
     * sub-expressions to null and sets complement to false.
     * 
     * @since   1.0 
     */
    protected void reset() {
        setSubject(null);
        setOperator(null);
        setValue(null);
        setComplement(false);
        subExpressionAndOperatorList = null;
    }

    @Override
    public String toString() {
        if (!isCompound()) {
            return toStringSimpleExpression();
        } else {
        	StringBuilder sb = new StringBuilder();
        	toStringCompoundExpression(this, sb);
        	return sb.toString();
        }
    }
    
    /**
     * Get the string of a simple expression.
     * 
     * @return  The string of a simple expression.
     * 
     * @since   1.0
     */
    private String toStringSimpleExpression() {
    	StringBuilder sb = new StringBuilder();
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
        sb.append("]");

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
    
    /**
     * Get the string of a simple expression.
     * 
     * <p>This method will use recursive way to get all the strings from the 
     * current expression and its nested expressions.
     * 
     * @param  expr
     *         The compound expression.
     *         
     * @param  sb
     *         The {@code StringBuilder} to collect all the strings from this 
     *         current expression and all the nested expressions.
     *         
     * @since   1.0
     */
    private void toStringCompoundExpression(Expression expr, StringBuilder sb) {
    	if (!expr.isCompound()) {
            sb.append(expr.toString());
            return;
        }

        if (expr.isComplement() == true) {
            sb.append("!");
        }

        sb.append("(");

        final int size = expr.getNumberOfSubExpression();

        for (int i = 0; i < size; i++) {
            Expression subExpr = expr.getSubExpression(i);

            if (i > 0) {
                sb.append(" ");
            }

            if (subExpr.isCompound()) {
            	toStringCompoundExpression(subExpr, sb);
            } else {
                sb.append(subExpr.toString());
            }

            if (i != (size - 1)) {
                sb.append(" ");
                sb.append(expr.getOperator(i, SIDE_RIGHT));
            }
        }
        sb.append(")");
    }

    /**
     * Converts the expression index into the real index of combined 
     * expressions and operators.
     * 
     * <p>The expression index is pseudo indexing of sub-expressions contained 
     * by the current compound expression.
     * 
     * <p>Sub-expressions are on even numbers starting at zero and the list of 
     * sub-expressions is the set of sub-expressions and operators, looks like: 
     * <pre>
     *     List = [ExprA, Optr, ExprB, Optr, ExprC, Optr, ExprD]
     * </pre>
     * 
     * <p>We can also consider there is the list of only sub-expressions like:
     * <pre>
     *     Expr = [ExprA, ExprB, ExprC, ExprD]
     * </pre>
     * 
     * <p>So you can see the relationship between the expression index for the 
     * sub-expression only list and the index of the sub-expressions and 
     * operators list:
     * <pre>
     *     Expr[0] = List[0] = ExprA
     *     Expr[1] = List[2] = ExprB
     *     Expr[2] = List[4] = ExprC
     *     Expr[3] = List[6] = ExprD
     * </pre>
     * 
     * @param  index
     *         The expression index.
     *         
     * @return  The index among the list of sub-expressions and operators mix.
     *          list.
     * 
     * @since   1.0
     */
    private int convertExpressionIndexToArrayIndex(int index) {
        return index * 2;
    }

    /**
     * Generate new expression representing minimized (sum-of-products) form 
     * of current expression.
     * 
     * <p>Product term: Combine 2 or more variables only by AND operator, like
     * <pre>
     * x
     * x * y
     * !x * !y
     * x * y * z
     * </pre>
     * 
     * <p>Sum-of-products: Do OR operations on a set of product terms, like
     * <pre>
     * x + (x * y) + (!x * !y) + (x * y * z)
     * </pre>
     * 
     * <p>Does not modify current expression instance.
     *
     * @return  The new {@code Expression} representing the (sum-of-products) 
     * of the current {@code Expression}.
     * 
     * @since   1.0
     */
    public Expression minimized() {
        return ExpressionEngine.minimize(this);
    }

    /**
     * Perform DFS (Depth-First-Search) of Expression tree.
     * 
     * <p>Consume both expression and logical operator nodes to produce binary 
     * pre-fix expression notation wherein the leading operator links the 
     * following sub-expressions. (Only "simple" Expressions will be produced. 
     * Every time an operator is encountered it indicates a new compound 
     * expression was encountered)
     * 
     * <pre>
     * ((A || B)  && C) -> &&, ||, A, B, C
     * (A || (B  && C)) -> || A, &&, B, C
     * (A && B && C)) -> &&, &&, A, B, C
     * </pre>
     * 
     * <p>
     * NOTE: postfix will produce "logical" evaluation which gives AND 
     * operations precedence over OR.  As a result, a new expression build 
     * from postfix evaluation will be logically equivalent, but may not be 
     * structurally identical to original expression.
     * 
     * <pre>
     * (A && B || C && D)) -> ||, && A, B, &&, C, D
     * </pre>
     *
     * @param  consumer
     *         The {@code Consumer} for performing an operation on each node.
     *         
     * @since   1.0
     */
    public void prefix(Consumer<Serializable> consumer) {
        if (!this.isCompound()) {
            consumer.accept(this);
        } else {
            // for any compound expression, its postfix representation either treats it as a single minterm (ALL AND clause)
            // (A && B && C) -> &&, &&, A, B, C
            // or the Sum-of-Products form of an OR'd set of 2 or more minterms
            // (A || B && C || D) ->  ||, ||, A, &&, B, C, D

            // calculate minterms
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

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     * 
     * Determines if two {@code Expression} are equal.
     * 
     * <p>Compound Expressions are considered equal only if they are 
     * structurally identical and contain all the same sub-expressions and 
     * operators in the same order.
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

        if (isCompound() != expression.isCompound()) {
        		// if one expression is simple and another is compound, 
        	    // they should be considered as different.
        		return false;
        } else {
        		if (!expression.isCompound()) {  
        			// if 2 expressions are simple
        			return isEqualSimpleExpressions(expression);
            } else {
            		// if 2 expressions are compound
            		return isEqualCompoundExpressions(expression);
            }
        }
    }
    
    /**
     * Check 2 simple expressions are equal or not.
     * 
     * <p>2 simple expressions are equal only if subject, operator, value and 
     * complement are same.
     * 
     * @param  expression
     *         The simple expression needs to be compared with this simple 
     *         expression.
     *         
     * @return  {@code true} if 2 simple expressions are same;
     *          {@code false} otherwise.
     *          
     * @since   1.0
     */
    private boolean isEqualSimpleExpressions(Expression expression) {
        // Subject
        if (getSubject() != null) {
            if (!getSubject().equals(expression.getSubject())) {
                return false;
            }
        } else if (expression.getSubject() != null) {
            return false;
        }

        // Operator
        if (getOperator() != null) {
            if (!getOperator().equals(expression.getOperator())) {
                return false;
            }
        } else if (expression.getOperator() != null) {
            return false;
        }

        // Value (check values for "equivalence", we don't care about being same object)
        if (getValue() != null) {
            if (!ReflectUtil2.equivalent(getValue(), expression.getValue())) {
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
    }
    
    /**
     * Check 2 compound expressions are equal or not.
     * 
     * @param  expression
     *         The compound expression needs to be compared with this compound 
     *         expression.
     *         
     * @return  {@code true} if 2 compound expressions are same;
     *          {@code false} otherwise.
     *          
     * @since   1.0
     */
    private boolean isEqualCompoundExpressions(Expression expression) {
    		// use size of sub-expressions to measure.
    	    // if size is not same, they should be different.
        if (getNumberOfSubExpression() != expression.getNumberOfSubExpression()) {
            return false;
        }

        // iterate over sub-expressions and operators
        // evaluate side-by-side comparisons
        for (int i = 0; i < getNumberOfSubExpression(); i++) {
        	    // compare sub-expression
            Expression exprA = getSubExpression(i);
            Expression exprB = expression.getSubExpression(i);
            
            if (exprA != null) {
                if (!exprA.equals(exprB)) {
                    return false;
                }
            } else {
                if (exprB != null) {
                    return false;
                }
            }

            // compare operator on the right side of the sub-expression
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

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     * 
     * Returns a deep copy of this {@code Expression} instance
     * 
     * <p>If an {@code Expression} is a simple expression (not a compound 
     * expression), it will not be truly cloned and its value is passed by 
     * reference.
     * 
     * @since   1.0
     */
    @Override
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

    /**
     * Clone all the sub-expressions from this {@code Expression} to 
     * the cloned {@code Expression}.
     * 
     * @param  currentExpr
     *         The {@code Expression} needs to be cloned.
     *         
     * @param  clonedExpr
     *         The new cloned {@code Expression}.
     *         
     * @since   1.0
     */
    private void clone(Expression currentExpr, Expression clonedExpr) {
        for (int i = 0; i < currentExpr.subExpressionAndOperatorList.size(); i++) {
            final Object o = currentExpr.subExpressionAndOperatorList.get(i);

            if (o instanceof Expression) {   // if it is an expression
                Expression childClone = (Expression) ((Expression) o).clone();
                clonedExpr.subExpressionAndOperatorList.add(childClone);
            } else {                         // if it is a operator
                clonedExpr.subExpressionAndOperatorList.add(o);
            }
        }
    }
}
