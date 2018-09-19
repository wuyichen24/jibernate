package personal.wuyi.jibernate.expression;

import org.apache.commons.lang3.time.DateUtils;
import org.testng.util.Strings;

import com.google.common.base.Preconditions;

import personal.wuyi.jibernate.util.StringUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The static class for evaluating, minimizing and manipulating expressions.
 * 
 * @author  Wuyi Chen
 * @date    08/30/2018
 * @version 1.0
 * @since   1.0
 */
public class ExpressionEngine {
	protected static final String  SIMPLE_EXPRESSION_REGEX   = "\\[(.*?)\\]\\s*(={2}|[<>]=?|!=|IN|LIKE|CONTAINS)\\s*(.*)";	
	protected static final Pattern SIMPLE_EXPRESSION_PATTERN = Pattern.compile(SIMPLE_EXPRESSION_REGEX, Pattern.CASE_INSENSITIVE);
	
	protected static final int     THRESHOLD = 500;

	private ExpressionEngine() {}
	
	/**
	 * Evaluate the truth value of an expression.
	 * 
	 * <p>This method will get all the minterms from the expression and check 
	 * at least one of the minterms is true.
	 * 
	 * @param  expr
	 *         The expression needs to be evaluated.
	 *         
	 * @return  {@code true} if at least one of minterms is true;
	 *          {@code false} otherwise.
	 *          
     * @since   1.0 
	 */
	public static boolean evaluate(Expression expr) {
		List<Expression> mintermList = ExpressionEngine.getMinterms(expr);
		
		boolean truth = false;
		for(Expression minterm : mintermList) {
			if(minterm.isCompound()) {        // if a minterm is compound, make sure all the sub-expression are true
				boolean minTruth = true;
				for (int i = 0; i < minterm.getNumberOfSubExpression(); i++) {
					Expression child = minterm.getSubExpression(i);
					boolean eval = evaluate(child.getSubject().getValue(), child.getOperator(), child.getValue());
					minTruth = minTruth && eval;
				}
				
				truth = truth || minTruth;
			} else {
				boolean eval = evaluate(minterm.getSubject().getValue(), minterm.getOperator(), minterm.getValue());
				truth = truth || eval;				
			}
		}	
		
		return truth;
	}
	
	/**
	 * Evaluate the truth value by comparing 2 objects.
	 * 
	 * <p>This method will compare 2 values and verify the operator can 
	 * reflect the relationship of 2 values truly.
	 * 
	 * <p>The 2 values can be {@code null}, but they should be in the same 
	 * type and also implemented the {@code Comparable} interface.
	 * 
	 * @param  a
	 *         The first operand.
	 *     
	 * @param  operator
	 *         The operator.
	 *         
	 * @param  b
	 *         The second operand.
	 *         
	 * @return  {@code true} if the operator can reflect the relationship of 
	 *                       2 values truly;
	 *          {@code false} otherwise.
	 *          
     * @since   1.0 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static boolean evaluate(Object a, String operator, Object b) {
		// check null
		if (a == null || b == null) {
			if (a == null && b == null) {
				return Expression.EQUAL.equals(operator);
			} else {
				return Expression.NOT_EQUAL.equals(operator);
			}
		}

		// check same type
		if(!a.getClass().isAssignableFrom(b.getClass()) || !b.getClass().isAssignableFrom(a.getClass())) {
			throw(new IllegalArgumentException(a.getClass().getName() + " is not comparable to " + b.getClass().getName()));
		}

		// check comparable
		if(!(a instanceof Comparable)) {
			throw new IllegalArgumentException(a.getClass().getName() + " is not comparable");
		}
		if(!(b instanceof Comparable)) {
			throw new IllegalArgumentException(b.getClass().getName() + " is not comparable");
		}

		Comparable p = (Comparable) a;
		Comparable q = (Comparable) b;

		switch (operator) {
        		case Expression.EQUAL:              return p.compareTo(q) == 0;
        		case Expression.NOT_EQUAL:          return p.compareTo(q) != 0;
        		case Expression.GREATER_THAN:       return p.compareTo(q) > 0;
        		case Expression.GREATER_THAN_EQUAL: return p.compareTo(q) >= 0;
        		case Expression.LESS_THAN:          return p.compareTo(q) < 0;
        		case Expression.LESS_THAN_EQUAL:    return p.compareTo(q) <= 0;
        		case Expression.STARTS_WITH:        return ((String) a).startsWith((String) b);
        		case Expression.ENDS_WITH:          return ((String) a).endsWith((String) b);
        		default: throw new IllegalArgumentException("Comparison " + operator + " not supported.");
		}		
	}
	
	/**
	 * Generates a list of sum-of-product minterms for a given compound expression. 
	 * 
	 * <p>Method will get the sum-of-product expression first and then collect 
	 * all the minterms.
	 *
	 * <p>For example, an expression:
	 * <pre>
	 * a * b + c * d * e + f
	 * </pre>
	 * <p>produces the set of minterms:
	 * <pre>
	 * {ab}, {cde}, {f}
	 * </pre>
	 * 
	 * @param  expression
	 *         The expression needs to get minterms.
	 *         
	 * @return  List of minterm expressions.
	 * 
     * @since   1.0 
	 */
	public static List<Expression> getMinterms(Expression expression) {
		if(expression == null) {
			return new ArrayList<>();
		}

		Expression sumOfProductExpr = getSumOfProducts(expression, THRESHOLD);
		return collectMintermAsExpression(sumOfProductExpr);
	}
	
	/**
	 * Collect minterms from an expression.
	 * 
	 * <p>Each minterm will be represented as {@code List<Expression>}.
	 * 
	 * @param  expr
	 *         The expression needs to be collected.
	 *         
	 * @return  A list of minterms.
	 * 
     * @since   1.0 
	 */
	protected static List<List<Expression>> collectMintermAsList(Expression expr) {
		List<List<Expression>> mintermList = new ArrayList<>();
		List<Expression>       minterm     = new ArrayList<>();

		for(int i = 0; i < expr.getNumberOfSubExpression(); i++) {
			Expression child     = expr.getSubExpression(i);
			String     rightOptr = i < expr.getNumberOfSubExpression() - 1 ? expr.getOperator(i, Expression.SIDE_RIGHT) : null;

			if(!minterm.contains(child)) {
				minterm.add(child);
			}

			if(rightOptr == null || !rightOptr.equals(Expression.AND)) {
				mintermList.add(minterm);
				minterm = new ArrayList<>();
			}
		}
		
		return mintermList;
	}
	
	/**
	 * Collect minterms from an expression.
	 * 
	 * <p>Each minterm will be represented as {@code Expression}.
	 * 
	 * @param  expr
	 *         The expression needs to be collected.
	 *         
	 * @return  A list of minterms.
	 * 
     * @since   1.0 
	 */
	protected static List<Expression> collectMintermAsExpression(Expression expr) {
		List<Expression> mintermList = new ArrayList<>();
		
		if(!expr.isCompound()) {                                           // sumOfProductExpr is a simple expression
			Expression minterm = new Expression();
			minterm.combineExpression(null, expr);
			mintermList.add(minterm);
		} else {
			Expression minterm = new Expression();

			for(int i = 0; i < expr.getNumberOfSubExpression(); i++) {
				Expression childExpr = expr.getSubExpression(i);
				String     rightOptr = i < expr.getNumberOfSubExpression() - 1 ? expr.getOperator(i, Expression.SIDE_RIGHT) : null;
				minterm.combineExpression(Expression.AND, childExpr);                 // collect all the sub-expressions for this minterms, like c * d * e

				if(rightOptr == null || rightOptr.equals(Expression.OR)) {            // if the next right operator is OR or null, add c * d * e into the mintermList
					mintermList.add(minterm);
					minterm = new Expression();
				}
			}
		}
		
		return mintermList;
	}

	/**
	 * Get the sum of products from an expression with threshold.
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
     * <p>If the number of sub-expressions is greater than the threshold, it 
     * will use the divide-and-conquor strategy for avoiding stack overflow. 
     * If not, it will use the stack solution.
	 * 
	 * @param  expr
	 *         The expression needs to be evaluated.
	 *         
	 * @param  threshold
	 *         The threshold for the size of one division in 
	 *         divide-and-conquor strategy.
	 *         
	 * @return  The sum of products from an expression.
	 * 
     * @since   1.0 
	 */
	protected static Expression getSumOfProducts(Expression expr, int threshold) {
		if(!expr.isCompound()) {
			return expr;
		} else {
			if(expr.getNumberOfSubExpression() > threshold) {
				return getSumOfProductsByDivideAndConquor(expr, threshold);
			} else {
				return getSumOfProductsByStack(expr);
			}
		}
	}
	
	/**
	 * Performs the logical sum of products expansion by divide-and-conquor 
	 * strategy.
	 * 
	 * <p>This method is fit for big expression for avoiding stack overflow.
	 * 
	 * @param  expr
	 *         The expression needs to be performed the sum of products 
	 *         expansion.
	 * 
	 * @param  threshold
	 *         The threshold for the size of one division in 
	 *         divide-and-conquor strategy.
	 *         
	 * @return  The expended sum of products expression.
	 * 
     * @since   1.0
	 */
	protected static Expression getSumOfProductsByDivideAndConquor(Expression expr, int threshold) {
		ArrayList<Expression> divisionList = new ArrayList<>();
		int p = 0;                // left bound
		int q = threshold - 1;    // right bound
		
		/*
		 * Divide the expression into divisions and add divisions into list
		 * */
		while(p <= q && q < expr.getNumberOfSubExpression()) {
			String rightOptr = expr.getOperator(q, Expression.SIDE_RIGHT);    // the right operator of the right bound

			if(rightOptr == null || rightOptr.equals(Expression.OR)) {        // if the right operator is OR, so the current minterms has been finished.    
				Expression division = new Expression();
				for(int i = p; i <= q; i++) {
					division.combineExpression(expr.getOperator(i, Expression.SIDE_LEFT), expr.getSubExpression(i));
				}
				divisionList.add(division);

				if(rightOptr == null) {         // if the right operator is null, it means the right bound hit the right end.
					break;
				}

				// advance the indices
				p = q + 1;
				q += threshold;
				if(q >= expr.getNumberOfSubExpression()) {
					q = expr.getNumberOfSubExpression() - 1;
				}
			} else {             // if the right operator is still AND, so move right bound to right by one expression.
				q++;              
			}
		}

		/*
		 * Conquor each division recursively.
		 * */
		if(divisionList.size() > 1) {
			Expression sop = new Expression();
			for(int i = 0; i < divisionList.size(); i++) {
				Expression subExpression = divisionList.get(i);
				Expression subSop        = getSumOfProducts(subExpression, THRESHOLD);

				if(!subSop.isCompound()) {
					sop.combineExpression(Expression.OR, subSop);
				} else {
					sop.combineCompoundExpression(Expression.OR, subSop);
				}
			}
			return sop;
		} else {
			/*
			 * if there is only one division, so shrink the threshold by half until there is more than one division.
			 * */
			if(threshold > 1) {
				threshold = threshold / 2;
				return getSumOfProducts(expr, threshold);
			} else {
				// FIXME: deal with all AND expression
				return getSumOfProductsByStack(divisionList.get(0));
			}
		}
	}

	/**
	 * Performs the logical sum of products expansion by stack.
	 *
	 * <p>This method is fit for small expression for avoiding stack overflow.
	 * 
	 * @param  expr
	 *         The expression needs to be performed the sum of products 
	 *         expansion.
	 * 
	 * @return  The expended sum of products expression.
	 * 
     * @since   1.0 
	 */
	protected static Expression getSumOfProductsByStack(Expression expr) {
		Deque<Object> exprStack = new ArrayDeque<>();  // the expression tree stack
		Deque<Object> pdaStack  = new ArrayDeque<>();  // pda stack
		Deque<Object> optrStack = new ArrayDeque<>();  // the operator stack

		// push initial expression onto the stack
		exprStack.push(expr);
		exprStack.push(0);

		// push compound complement
		if(expr.isComplement()) {
			optrStack.push("!");
		}

		if(expr.isCompound()) {
			optrStack.push("_");
		}
		
		while(!exprStack.isEmpty()) {
			boolean descend = false;

			int        currentIndex = ((Integer)   exprStack.pop()).intValue();    // get the current expression index
			Expression currentExpr  = (Expression) exprStack.pop();                // get the current expression

			for(int i = currentIndex; !descend && i < currentExpr.getNumberOfSubExpression(); i++) {
				Expression child = currentExpr.getSubExpression(i);

				if(child.isCompound()) {         
					child = simplifyNestedExpression(child);
				}

				String leftOptr  = i > 0                                   ? currentExpr.getOperator(i, Expression.SIDE_LEFT)  : null; // the left operator
				String rightOptr = i < expr.getNumberOfSubExpression() - 1 ? currentExpr.getOperator(i, Expression.SIDE_RIGHT) : null; // the right operator

				if(!child.isCompound()) {
					if(rightOptr != null) {
						// give AND precedence
						if(leftOptr != null && leftOptr.equals(Expression.AND)) {
							Expression popExpr = (Expression) pdaStack.pop();
							pdaStack.push(intersection(popExpr, child));
						} else {
							if(leftOptr != null) {
								optrStack.push(leftOptr);
							}
							pdaStack.push(child);
						}
					} else {
						if(leftOptr != null) {
							optrStack.push(leftOptr);
						}
						pdaStack.push(child);
						reduce(optrStack, pdaStack, false);
					}
				} else {
					// push the operator and stack separator
					// to indicate compound expression reduction
					if(leftOptr != null) {
						optrStack.push(leftOptr);
					}

					// push compound complement
					if(child.isComplement()) {
						optrStack.push("!");
					}

					// push stack seperator
					optrStack.push("_");

					// save parent position
					exprStack.push(currentExpr);
					exprStack.push(i + 1);

					// make the child the current expr
					currentExpr = child;

					// push child onto minimize stack
					exprStack.push(child);
					exprStack.push(0);

					// descend branch to minimize child
					descend = true;
				}
			}
		}

		reduce(optrStack, pdaStack, true);
		Expression sop = (Expression) pdaStack.pop();

		if(sop.isCompound()) {
			sop = simplify(sop);
			sop = ExpressionEngine.simplifyNestedExpression(sop);
		}

		return sop;
	}

	/**
	 * Reduce the pda stack.
	 * 
	 * <p>This method will loop through the operator stack and use each 
	 * operator to reduce the size of the pda stack.
	 * 
	 * @param  operStack
	 *         The operator stack.
	 *         
	 * @param  pdaStack
	 *         The pda stack.
	 * 
	 * @param  reduceAll
	 *         The flag to indicate just reduce the current expression or all 
	 *         the nested expressions.
	 *         
     * @since   1.0 
	 */
	protected static void reduce(Deque<Object> operStack, Deque<Object> pdaStack, boolean reduceAll) {
		while(!operStack.isEmpty()) {
			String operator = (String) operStack.pop();

			switch (operator) {
            	case "_": reduceForStackOperator(operStack, pdaStack, reduceAll); break;
            	case "!": reduceForComplementOperator(pdaStack);                  break;
            	default:  reduceForAndOrOperator(operator, pdaStack);             break;
			}
		}
	}
	
	/**
	 * Reduce the pda stack on current stack operator.
	 * 
	 * @param  operStack
	 *         The operator stack.
	 *         
	 * @param  pdaStack
	 *         The pda stack.
	 *         
	 * @param  reduceAll
	 *         The flag to indicate just reduce the current expression or all 
	 *         the nested expressions.
	 *         
     * @since   1.0 
	 */
	protected static void reduceForStackOperator(Deque<Object> operStack, Deque<Object> pdaStack, boolean reduceAll) {
		if(!reduceAll) {
			while(!operStack.isEmpty() && !((String) operStack.peek()).equals("_")) {
				String op = (String) operStack.pop();

				if(op.equals("!")) {
					reduceForComplementOperator(pdaStack);
				} else {
					reduceForAndOrOperator(op, pdaStack);
				}
			}
		}
	}
	
	/**
	 * Reduce the pda stack on current complement operator.
	 * 
	 * <p>Complement operation turns SOP expr into POS, like:
	 * <pre>
	 *   !(AB + C + !DE) ==> (!A + !B) * !C * (D + !E)
	 * </pre>
	 * <p>so this method will multiply out, like:
	 * <pre>
	 *   (!A + !B) * !C * (D + !E) ==> !A!CD + !A!C!E + !B!CD + !B!C!E
	 * </pre>
	 *         
	 * @param  pdaStack
	 *         The pda stack.
	 *         
     * @since   1.0 
	 */
	protected static void reduceForComplementOperator(Deque<Object> pdaStack) {
		Expression complementExpr = (Expression) pdaStack.pop();
		
		if(complementExpr.isCompound()) {  // compound expression
			complementExpr.complement(true);
			if(complementExpr.getNumberOfSubExpression() == 1) {
				complementExpr = simplifyNestedExpression(complementExpr);
			} else {
				Expression temp = complementExpr.getSubExpression(0);
				for(int i = 1; i < complementExpr.getNumberOfSubExpression(); i++) {
					Expression subExpr = complementExpr.getSubExpression(i);
					temp = intersection(temp, subExpr);
				}
				complementExpr = temp;
			}
		} else {                            // simple expression
			Expression temp = (Expression) complementExpr.clone();
			complementExpr = temp;
			complementExpr.complement();
		}

		pdaStack.push(complementExpr);
	}
	
	/**
	 * Reduce the pda stack on current AND / OR operator.
	 * 
	 * @param  operator
	 *         The current operator.
	 *        
	 * @param  pdaStack
	 *         The pda stack.
	 * 
     * @since   1.0
	 */
	protected static void reduceForAndOrOperator(String operator, Deque<Object> pdaStack) {
		Expression expr2 = (Expression) pdaStack.pop();
		Expression expr1 = (Expression) pdaStack.pop();

		if(operator.equals(Expression.OR)) {
			pdaStack.push(union(expr1, expr2));
		} else {
			pdaStack.push(intersection(expr1, expr2));
		}
	}

	/**
	 * Simplify an expression by the Idempotence, Commutativity and 
	 * Absorption in boolean algebra.
	 * 
	 * <ul>
	 *   <li>Idempotence
	 *     <pre>
	 *       E * E ==> E
	 *       E + E ==> E
	 *     </pre>
	 *   <li>Commutativity
	 *     <pre>
	 *       AB + BA + CD ==> AB + CD
	 *     </pre>
	 *   <li>Absorption
	 *     <pre>
	 *       A + AB      ==> A
	 *       A * (A + B) ==> AA + AB ==> A + AB ==> A
	 *     </pre>
	 * </ul>
	 * 
	 * @param  expr
	 *         The expression needs to be simplified.
	 *         
	 * @return  The simplified expression.
	 * 
     * @since   1.0
	 */
	protected static Expression simplify(Expression expr) {
		Expression minimized = expr;

		List<List<Expression>> mintermList = collectMintermAsList(minimized);
		removeRedundantMinterms(mintermList);
		return mergeMintermsAsOneExpression(mintermList);
	}
	
	/**
	 * Remove redundant minterms from the minterms list.
	 * 
	 * <p>This method uses fast-slow pointer strategy to remove redundant minterms.
	 * 
	 * @param  mintermList
	 *         The list of minterms without redundant elements.
	 *         
     * @since   1.0
	 */
	protected static void removeRedundantMinterms(List<List<Expression>> mintermList) {
		int p = 0;
		int q = 0;
		while(p < mintermList.size()) {
			List<Expression> current = mintermList.get(p);

			while(q < mintermList.size()) {
				if(p != q) {
					List<Expression> next = mintermList.get(q);

					if(current.containsAll(next)) {
						mintermList.remove(p);
						q = 0;
						break;
					}

					if(next.containsAll(current)) {
						mintermList.remove(q);

						if(q < p) {
							p--;
						}
					} else {
						q++;
					}

				} else {
					q++;
				}

				if(q == mintermList.size()) {
					p++;
				}
			}
			q = 0;
		}
	}
	
	/**
	 * Merge a list of minterms as one expression.
	 * 
	 * <p>For expressions in a minterm, they will be merged by AND. Between 
	 * minterms, they will be merged by OR. For example:
	 * <pre>
	 *   {{A, B}, {C, D}, {E}} ==> A B + C D + E
	 * <pre>
	 * 
	 * @param  mintermList
	 *         The list of minterms.
	 *         
	 * @return  The merged expression.
	 * 
     * @since   1.0
	 */
	protected static Expression mergeMintermsAsOneExpression(List<List<Expression>> mintermList) {
		Expression expr = new Expression();

		for(int m = 0; m < mintermList.size(); m++) {
			List<Expression> minterm = mintermList.get(m);

			for(int n = 0; n < minterm.size(); n++) {
				if(n == 0) {
					expr.combineExpression(Expression.OR, minterm.get(n));
				} else {
					expr.combineExpression(Expression.AND, minterm.get(n));
				}
			}
		}

		return expr;
	}

	/**
	 * Apply the associative law (union / plus) for 2 expressions.
	 * 
	 * <pre>
	 *   Union(A, B) = A + B
	 * </pre>
	 * 
	 * @param  e1
	 *         The first expression.
	 *         
	 * @param  e2
	 *         The second expression.
	 *         
	 * @return  The union of 2 expressions.
	 * 
     * @since   1.0
	 */
	protected static Expression union(Expression e1, Expression e2) {
		Expression union = null;
		
		if(!e1.isCompound()) {
			if(!e2.isCompound()) {
				union = new Expression();
				union.combineExpression(null, e1);
				union.combineExpression(Expression.OR, e2);
			} else {
				union = e2;
				union.addSubExpressionWithOperator(0, e1, Expression.OR);
			}
		} else {
			union = e1;
			if(!e2.isCompound()) {
				union.combineExpression(Expression.OR, e2);
			} else {
				union.combineCompoundExpression(Expression.OR, e2);
			}
		}

		return union;
	}

	/**
	 * Apply the distributive law (intersect / multiply) for 2 expressions.
	 * 
	 * <pre>
	 *   Intersection(A, B) = A * B
	 * </pre>
	 * 
	 * @param  e1
	 *         The first expression.
	 *         
	 * @param  e2
	 *         The second expression.
	 *         
	 * @return  The intersection of 2 expressions.
	 * 
     * @since   1.0
	 */
	protected static Expression intersection(Expression e1, Expression e2) {
		Expression intersection = new Expression();
		
		if(!e1.isCompound()) {
			if(!e2.isCompound()) {
				intersection.combineExpression(null, e1);
				intersection.combineExpression(Expression.AND, e2);
				return intersection;
			} else {                // Case 2: A * (B + C) ==> A * B + A * C
				return intersectionSimpleExpressionWithCompoundExpression(e1, e2);
			}
		} else {        
			if(!e2.isCompound()) {  // Case 3: (B + C) * A ==> A * B + A * C
				return intersectionSimpleExpressionWithCompoundExpression(e2, e1);
			} else {                // Case 4: (A + B) * (C + D) ==> AC + AD + BC + BD
				return intersectionCompoundExpressionWithCompoundExpression(e1, e2);
			}
		}
	}
	
	/**
	 * Apply the distributive law (intersect / multiply) for one simple 
	 * expression and one compound expression.
	 * 
	 * <p>This method works as:
	 * <pre>
	 *    A * (B + C) ==> A * B + A * C
	 * </pre>
	 * 
	 * @param  simpleExpr
	 *         The simple expression.
	 *         
	 * @param  compoundExpr
	 *         The compound expression.
	 *         
	 * @return  The intersection of 2 expressions.
	 * 
     * @since   1.0
	 */
	protected static Expression intersectionSimpleExpressionWithCompoundExpression(Expression simpleExpr, Expression compoundExpr) {
		Preconditions.checkArgument(!simpleExpr.isCompound(), "The first expression should be simple, but currently it is " + simpleExpr.toString());
		Preconditions.checkArgument(!simpleExpr.isCompound(), "The first expression should be compound, but currently it is " + compoundExpr.toString());
		
		Expression intersection = new Expression();
		
		for(int i = 0; i < compoundExpr.getNumberOfSubExpression(); i++) {
			Expression subExpr = compoundExpr.getSubExpression(i);
			String     leftOptr  = i > 0 ? compoundExpr.getOperator(i, Expression.SIDE_LEFT) : null;

			if(leftOptr == null || leftOptr.equals(Expression.OR)) {
				intersection.combineExpression(Expression.OR, simpleExpr);
			}

			if(!subExpr.equals(simpleExpr)) {
				intersection.combineExpression(Expression.AND, subExpr);
			}
		}
		
		return intersection;
	}
	
	/**
	 * Apply the distributive law (intersect / multiply) for one compound 
	 * expression and one compound expression.
	 * 
	 * <p>This method works as:
	 * <pre>
	 *    (A + B) * (C + D) ==> AC + AD + BC + BD
	 * </pre>
	 * 
	 * @param  comExpr1
	 *         The first compound expression.
	 *         
	 * @param  comExpr2
	 *         The second compound expression.
	 *         
	 * @return  The intersection of 2 expressions.
	 * 
     * @since   1.0
	 */
	protected static Expression intersectionCompoundExpressionWithCompoundExpression(Expression comExpr1, Expression comExpr2) {
		Expression intersection = new Expression();
		
		List<Expression> minterm1 = new ArrayList<>();
		List<Expression> minterm2 = new ArrayList<>();

		for(int i = 0; i < comExpr1.getNumberOfSubExpression(); i++) {
			Expression subExpr1  = comExpr1.getSubExpression(i);
			String     rightOptr = i < comExpr1.getNumberOfSubExpression() - 1 ? comExpr1.getOperator(i, Expression.SIDE_RIGHT) : null;
			minterm1.add(subExpr1);

			if(rightOptr == null || rightOptr.equals(Expression.OR)) {
				for(int j = 0; j < comExpr2.getNumberOfSubExpression(); j++) {
					Expression subExpr2 = comExpr2.getSubExpression(j);
					String     rightOptr2 = i < comExpr2.getNumberOfSubExpression() - 1 ? comExpr2.getOperator(i, Expression.SIDE_RIGHT) : null;
					minterm2.add(subExpr2);

					if(rightOptr2 == null || rightOptr2.equals(Expression.OR)) {
						for(int p = 0; p < minterm1.size(); p++) {
							if(p == 0) {
								intersection.combineExpression(Expression.OR, (Expression) minterm1.get(p));
							} else {
								intersection.combineExpression(Expression.AND, (Expression) minterm1.get(p));
							}
						}

						for(int q = 0; q < minterm2.size(); q++) {
							intersection.combineExpression(Expression.AND, (Expression) minterm2.get(q));
						}

						minterm2 = new ArrayList<>();
					}
				}
				minterm1 = new ArrayList<>();
			}
		}

		return simplify(intersection);
	}

	/**
     * Simplify a nested expression.
     * 
     * <p>If a compound expression only has one sub-expression, this method 
     * will take out the sub-expression as a standalone expression and also 
     * preserve the complement. For example, 
     * 
     * <pre>
     * 		((E))      ==> E
     *      (((E)))    ==> E
     *      (!(!E))    ==> E
     *      (!(!(!E))) ==> !E
     * <pre>
     * 
     * @param  expression
     *         The compound expression needs to be simplified.
     *         
     * @return  The new standalone expression.
     * 
     * @since   1.0 
     */
    public static Expression simplifyNestedExpression(Expression expr) {    		
    		if(!expr.isCompound()) {
    			return expr;
    		}

    		Expression temp = expr;

    		while(temp.getNumberOfSubExpression() == 1) {
    			boolean complement = temp.isComplement();   // preserve the complement
    			temp = temp.getSubExpression(0);
    			if(complement) {
    				temp.complement();
    			}
    		}

    		return temp;
    }
	
	/**
	 * Get the sum of products from an expression.
	 * 
     * <p>Product term: Combine 2 or more variables only by AND operator, like
     * <pre>
     *   x
     *   x * y
     *   !x * !y
     *   x * y * z
     * </pre>
     * 
     * <p>Sum-of-products: Do OR operations on a set of product terms, like
     * <pre>
     *   x + (x * y) + (!x * !y) + (x * y * z)
     * </pre>
	 * 
	 * @param  expr
	 *         The expression needs to be evaluated.
	 *         
	 * @return  The sum of products from an expression.
	 * 
     * @since   1.0 
	 */
	public static Expression getSumOfProducts(Expression expr) {
		Preconditions.checkNotNull(expr, "The input expression is null");
		return getSumOfProducts(expr, THRESHOLD);
	}


	/**
	 * Parse the string into an expression
	 * 
	 * @param  input
	 *         The input string.
	 * 
	 * @return  The expression by parsing
	 * 
     * @since   1.0
	 */
	public static Expression parse(String input) {
		if(Strings.isNullOrEmpty(input)) {
			return null;
		}

		// normalize logical operators
		input = StringUtil.replace(input, "AND", "&&", true, true);
		input = StringUtil.replace(input, "OR", "||", true, true);

		int length = input.length();
		Deque<Character>  stack           = new ArrayDeque<>();   // store the current expression string by parenthesis
		Deque<Expression> expressionStack = new ArrayDeque<>();   // store the expressions has been parsed.

		int index = 0;
		while(index < length) {
			char currentChar = input.charAt(index);

			if(currentChar == ')') {    // when meet balanced parenthesis, evaluate the values in stack
				StringBuilder sb = new StringBuilder();
				
				while(!stack.isEmpty()) {
					currentChar = stack.pop();
					if(currentChar == '(') {
						String expr = sb.toString();

						Matcher matcher = SIMPLE_EXPRESSION_PATTERN.matcher(expr);
						if(matcher.find() && matcher.groupCount() == 3) {
							String subject  = matcher.group(1);
							String operator = matcher.group(2);
							String value    = matcher.group(3);

							Object obj = parseValue(value);

							Expression simpleExpr = new Expression(subject, operator, obj);
							expressionStack.push(simpleExpr);
							break;
						} else {
							reduceExpression(stack, expressionStack);
							break;
						}
					} else if(currentChar == '&' || currentChar == '|') {
						stack.push(currentChar);
						reduceExpression(stack, expressionStack);
						break;
					} else {
						sb.insert(0, currentChar);
					}
				}
			} else {
				stack.push(currentChar);
			}

			index++;
		}

		if(!stack.isEmpty()) {
			reduceExpression(stack, expressionStack);
		}

		if (expressionStack.isEmpty()) {
			return null;
		} else {
			return expressionStack.pop();
		}
	}
	
	/**
	 * Parse the value in an expression.
	 * 
	 * <p>Currently this method supports different types of values:
	 * <ul>
	 *   <li>null
	 *   <li>String
	 *   <li>Boolean
	 *   <li>Date
	 *   <li>Integer
	 *   <li>Double
	 *   <li>List
	 * </ul>
	 * 
	 * @param  value
	 *         The value needs to be parsed.
	 *         
	 * @return  The {@code Object} represents the value.
	 * 
     * @since   1.0
	 */
	protected static Object parseValue(String value) {
		if(value.startsWith("\"") && value.endsWith("\"")) {     // string
			return value.substring(1, value.length() - 1);
		} else if(value.startsWith("[") && value.endsWith("]")) {  // list
			value = value.substring(1, value.length() - 1);
			String[] values = value.split(",");
			List<Object> list = new ArrayList<>();
			
			for(String s : values) {
			    if(s.startsWith("\"")) {
                    s = s.replaceAll("^\"|\"$", "");
                    list.add(s);
                } else {
                    Object v = Integer.valueOf(s.trim());
                    list.add(v);
                }
			}
			
			return list;
		} else if("null".equalsIgnoreCase(value)) {
			return null;
		} else if("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
			return Boolean.valueOf(value);
		} else {
			try {
				return DateUtils.parseDate(value, new String[] {"MM/dd/yy","MM/dd/yy HH:mm:ss", "E MMM d HH:mm:ss z yyyy"});
			} catch(Exception e) {
				boolean isInteger = Pattern.matches("^\\d*$", value);
				if(isInteger) {
					return Integer.valueOf(value);
				} else {
					return Double.valueOf(value);
				}
			}
		}
	}

	/**
	 * Reduce the complexity of the expression stack.
	 * 
	 * @param  stack
	 *         The character stack.
	 *         
	 * @param  expressionStack
	 *         The expression stack.
	 *        
     * @since   1.0  
	 */
	protected static void reduceExpression(Deque<Character> stack, Deque<Expression> expressionStack) {
		if(!stack.isEmpty() && !expressionStack.isEmpty()) {
			Expression compoundExpr = new Expression();

			Expression subExpr = expressionStack.pop();
			compoundExpr.combineExpression(null, subExpr);
			
			while(!stack.isEmpty()) {
				char current = stack.pop();

				if(current == '(') {
					break;
				} else if(current == ' ') {    // skip white space
					continue;
				}

				if(isLogicalOperator(current)) {
					StringBuilder sb = new StringBuilder();
					sb.append(current).append(stack.pop());

					subExpr = expressionStack.pop();
					compoundExpr.addSubExpressionWithOperator(0, subExpr, sb.toString());
				}
			}

			expressionStack.push(compoundExpr);
		}
	}

	/**
	 * Check a character is a comparison operator or not.
	 * 
	 * @param  ch
	 *         The char needs to be checked.
	 *         
	 * @return  {@code true} if the char is a comparison operator;
	 *          {@code false} otherwise.
	 * 
     * @since   1.0
	 */
	protected static boolean isComparisonOperator(char ch) {
		return (ch == '=' || ch == '<' || ch == '>' || ch == '!');
	}

	/**
	 * Check a character is a logical operator or not.
	 * 
	 * @param  ch
	 *         The char needs to be checked.
	 *         
	 * @return  {@code true} if the char is a logical operator;
	 *          {@code false} otherwise.
	 *          
     * @since   1.0
	 */
	protected static boolean isLogicalOperator(char ch) {
		return(ch == '&' || ch == '|');
	}
}
