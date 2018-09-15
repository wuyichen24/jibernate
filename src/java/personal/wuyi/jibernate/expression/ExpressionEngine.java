package personal.wuyi.jibernate.expression;

import org.apache.commons.lang3.time.DateUtils;

import personal.wuyi.jibernate.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
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
	protected final static String  SIMPLE_EXPRESSION_REGEX   = "\\[(.*?)\\]\\s*(={2}|[<>]=?|!=|IN|LIKE|CONTAINS)\\s*(.*)";	
	protected final static Pattern SIMPLE_EXPRESSION_PATTERN = Pattern.compile(SIMPLE_EXPRESSION_REGEX, Pattern.CASE_INSENSITIVE);
	
	protected final static int     THRESHOLD = 500;

	
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
		List<Expression> mintermList = new ArrayList<Expression>();
		
		if(expression == null) {
			return mintermList;
		}

		Expression sumOfProductExpr = getSumOfProducts(expression, THRESHOLD);

		if(!sumOfProductExpr.isCompound()) {                   // sumOfProductExpr is a simple expression
			Expression minterm = new Expression();
			minterm.combineExpression(null, sumOfProductExpr);
			mintermList.add(minterm);
		} else {
			Expression minterm = new Expression();

			for(int i = 0; i < sumOfProductExpr.getNumberOfSubExpression(); i++) {
				Expression childExpr = sumOfProductExpr.getSubExpression(i);
				String     rightOptr = i < sumOfProductExpr.getNumberOfSubExpression() - 1 ? sumOfProductExpr.getOperator(i, Expression.SIDE_RIGHT) : null;
				minterm.addSubExpressionWithOperator(childExpr, Expression.AND);      // collect all the sub-expressions for this minterms, like c * d * e

				if(rightOptr == null || rightOptr.equals(Expression.OR)) {            // if the next right operator is OR or null, add c * d * e into the mintermList
					mintermList.add(minterm);
					minterm = new Expression();
				}
			}
		}

		return mintermList;
	}

	/**
	 * Get the sum of products from an expression.
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
	private static Expression getSumOfProductsByDivideAndConquor(Expression expr, int threshold) {
		ArrayList<Expression> divisionList = new ArrayList<Expression>();
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
					division.addSubExpressionWithOperator(expr.getSubExpression(i), expr.getOperator(i, Expression.SIDE_LEFT));
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
				Expression subExpression = (Expression) divisionList.get(i);
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
				return null;
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
	private static Expression getSumOfProductsByStack(Expression expr) {
		Stack<Object> exprStack = new Stack<>();  // the expression tree stack
		Stack<Object> pdaStack  = new Stack<>();  // pda stack
		Stack<Object> optrStack = new Stack<>();  // operator stack

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

				String leftOptr  = i > 0                                   ? expr.getOperator(i, Expression.SIDE_LEFT)  : null; // the left operator
				String rightOptr = i < expr.getNumberOfSubExpression() - 1 ? expr.getOperator(i, Expression.SIDE_RIGHT) : null; // the right operator

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
	 * Do the pda stack reduction. Applies shift/reduction rules.
	 */
	private static void reduce(Stack operStack, Stack pdaStack, boolean reduceAll) {

		while(!operStack.isEmpty()) {
			String operator = (String) operStack.pop();

			if(operator.equals("_")) {
				// Hit the stack separator so try to see if we can reduce any ANDS,
				// otherwise break out of reduction loop
				if(reduceAll == false) {
					while(!operStack.isEmpty() &&
					// !((String)operStack.peek()).equals(Expression.OR) &&
							!((String) operStack.peek()).equals("_")) {

						String op = (String) operStack.pop();

						if(op.equals("!")) {
							Expression complementExpr = (Expression) pdaStack.pop();

							if(complementExpr.isCompound()) {
								complementExpr.complement(true);

								if(complementExpr.getNumberOfSubExpression() == 1) {
									complementExpr = simplifyNestedExpression(complementExpr);
								}
								else {
									// complement operation turns SOP expr into POS
									// !(AB + C + !DE) <==> (!A + !B) * !C * (D + !E)
									// so we need to multiply out
									// !A!CD + !A!C!E + !B!CD + !B!C!E
									Expression temp = complementExpr.getSubExpression(0);
									for(int i = 1; i < complementExpr.getNumberOfSubExpression(); i++) {
										Expression blah = complementExpr.getSubExpression(i);
										temp = intersection(temp, blah);
									}
									complementExpr = temp;
								}
							}
							else {
								Expression temp = (Expression) complementExpr.clone();
								complementExpr = temp;
								complementExpr.complement();
							}
							
							pdaStack.push(complementExpr);
						}
						else if(op.equals(Expression.OR)) {
							Expression expr2 = (Expression) pdaStack.pop();
							Expression expr1 = (Expression) pdaStack.pop();

							pdaStack.push(union(expr1, expr2));
						}
						else {
							Expression expr2 = (Expression) pdaStack.pop();
							Expression expr1 = (Expression) pdaStack.pop();

							pdaStack.push(intersection(expr1, expr2));
						}
					}

					break;
				}
			}
			else if(operator.equals("!")) {
				Expression complementExpr = (Expression) pdaStack.pop();
				if(complementExpr.isCompound()) {
					complementExpr.complement(true);
					if(complementExpr.getNumberOfSubExpression() == 1) {
						complementExpr = simplifyNestedExpression(complementExpr);
					}
					else {
						// complement operation turns SOP expr into POS
						// !(AB + C + !DE) <==> (!A + !B) * !C * (D + !E)
						// so we need to multiply out
						// !A!CD + !A!C!E + !B!CD + !B!C!E
						Expression temp = complementExpr.getSubExpression(0);
						for(int i = 1; i < complementExpr.getNumberOfSubExpression(); i++) {
							Expression blah = complementExpr.getSubExpression(i);
							temp = intersection(temp, blah);
						}
						complementExpr = temp;
					}
				}
				else {
					Expression temp = (Expression) complementExpr.clone();
					complementExpr = temp;
					complementExpr.complement();
				}

				pdaStack.push(complementExpr);
			}
			else {
				Expression expr2 = (Expression) pdaStack.pop();
				Expression expr1 = (Expression) pdaStack.pop();

				if(operator.equals(Expression.OR)) {
					pdaStack.push(union(expr1, expr2));
				}
				else {
					pdaStack.push(intersection(expr1, expr2));
				}
			}
		}
	}


	/**
	 * Takes an expanded expression and attempts to simplify. ASSERTION: The expression is compound and expanded (flat)
	 */
	private static Expression simplify(Expression expr) {

		// /////////// IDEMPOTENCE, COMMUTATIVITY, SIMPLIFICATION \\\\\\\\\\\\\\\\\\
		//
		// Remove equivalent terms from disjunctive expression
		//
		// E * E == E (Idempotence)
		// E + E = E (Idempotence)
		// AB + CD + BA == AB + CD (Commutativity)
		// E + EZ == E(true + Z) == E (Simplification)
		//
		// ASSERTION: expression consists only of flattened minterm
		//
		// //////////////////////////////////////////////////////////////////////////////

		Expression minimized = expr;

		List<List<Expression>> mintermList = new ArrayList<List<Expression>>();
		List<Expression> minterm = new ArrayList<Expression>();

		// build Arraylist of minterms
		for(int i = 0; i < minimized.getNumberOfSubExpression(); i++) {

			Expression child = minimized.getSubExpression(i);
			String la = (i + 1 == minimized.getNumberOfSubExpression() ? null : minimized.getOperator(i, Expression.SIDE_RIGHT));

			if(!minterm.contains(child))
				minterm.add(child);

			// dump when the next or'd term or the end of the expression is reached
			if(la == null || !la.equals(Expression.AND)) {

				mintermList.add(minterm);
				minterm = new ArrayList<Expression>();
			}
		}

		// simplify by removing redundant minterms
		int p = 0, q = 0;
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

						if(q < p)
							p--;

					}
					else {

						q++;
					}

				}
				else {

					q++;
				}

				if(q == mintermList.size()) {
					p++;
				}
			}
			q = 0;
		}

		minimized = new Expression();

		// recontruct the new minimized expression
		for(int m = 0; m < mintermList.size(); m++) {

			minterm = mintermList.get(m);

			for(int n = 0; n < minterm.size(); n++) {

				if(n == 0) {

					minimized.addSubExpressionWithOperator(minterm.get(n), Expression.OR);

				}
				else {

					minimized.addSubExpressionWithOperator(minterm.get(n), Expression.AND);
				}
			}
		}

		return minimized;
	}


	/**
	 * Apply the complement to evaluated result and do double negation if necessary.
	 * 
	 * 
	 */
	private static boolean doUnaryOperation(Expression expr, boolean evaluation) {

		if(expr.isComplement()) {

			return(!evaluation);

		}
		else {

			return(evaluation);
		}
	}


	/**
	 * performs union of two minimized expressions
	 */
	private static Expression union(Expression e1, Expression e2) {

		Expression union = null;
		if(e1.isCompound() == false) {

			if(e2.isCompound() == false) {

				union = new Expression();
				union.addSubExpressionWithOperator(e1, null);
				union.addSubExpressionWithOperator(e2, Expression.OR);
			}
			else {

				union = e2;
				union.addSubExpressionWithOperator(0, e1, Expression.OR);
			}
		}
		else {

			if(e2.isCompound() == false) {

				union = e1;
				union.addSubExpressionWithOperator(e2, Expression.OR);
			}
			else {

				union = e1;
				union.addCompoundExpression(e2, Expression.OR);
			}
		}

		return union;
	}


	/**
	 * performs intersection of two minimized expressions
	 */
	private static Expression intersection(Expression e1, Expression e2) {

		Expression intersection = null;
		if(e1.isCompound() == false) {

			if(e2.isCompound() == false) {

				intersection = new Expression();
				intersection.addSubExpressionWithOperator(e1, null);
				intersection.addSubExpressionWithOperator(e2, Expression.AND);

			}
			else {

				intersection = new Expression();

				for(int i = 0; i < e2.getNumberOfSubExpression(); i++) {

					Expression child = (Expression) e2.getSubExpression(i);
					String op = (i == 0 ? null : e2.getOperator(i, Expression.SIDE_LEFT));

					if(op == null || op.equals(Expression.OR)) {
						intersection.addSubExpressionWithOperator(e1, Expression.OR);
					}

					// simplify redundant terms
					// A * AB ==> AB
					if(!child.equals(e1)) {
						intersection.addSubExpressionWithOperator(child, Expression.AND);
					}
				}
			}

		}
		else {

			if(e2.isCompound() == false) {

				intersection = new Expression();

				boolean matched = false;
				for(int i = 0; i < e1.getNumberOfSubExpression(); i++) {

					Expression child = e1.getSubExpression(i);
					String op = (i == 0 ? null : e1.getOperator(i, Expression.SIDE_LEFT));
					String la = (i + 1 == e1.getNumberOfSubExpression() ? null : e1.getOperator(i, Expression.SIDE_RIGHT));

					intersection.addSubExpressionWithOperator(child, op);

					// simplify redundant terms
					// A * AB ==> AB
					if(child.equals(e2)) {
						matched = true;
					}

					if(la == null || la.equals(Expression.OR)) {
						if(matched == false) {
							intersection.addSubExpressionWithOperator(e2, Expression.AND);
						}
						matched = false;
					}

				}

			}
			else {

				intersection = new Expression();

				List<Expression> minterm1 = new ArrayList<Expression>();
				List<Expression> minterm2 = new ArrayList<Expression>();

				// calculate product minterms
				for(int i = 0; i < e1.getNumberOfSubExpression(); i++) {
					Expression alpha = e1.getSubExpression(i);
					String op1 = (i + 1 == e1.getNumberOfSubExpression() ? null : e1.getOperator(i, Expression.SIDE_RIGHT));

					minterm1.add(alpha);

					if(null == op1 || op1.equals(Expression.OR)) {
						for(int j = 0; j < e2.getNumberOfSubExpression(); j++) {
							Expression beta = e2.getSubExpression(j);
							String op2 = (j + 1 == e2.getNumberOfSubExpression() ? null : e2.getOperator(j, Expression.SIDE_RIGHT));

							minterm2.add(beta);

							if(null == op2 || op2.equals(Expression.OR)) {
								// create "flat" intersection of "ANDED" minterms so:
								// (A*B + C)*(D*E + F*G) = (A*B*D*E + A*B*F*G + C*D*E + C*F*G)
								for(int p = 0; p < minterm1.size(); p++) {
									// "OR" in the fist "ANDED" minterm
									if(p == 0) {
										intersection.addSubExpressionWithOperator((Expression) minterm1.get(p), Expression.OR);
									}
									else {
										intersection.addSubExpressionWithOperator((Expression) minterm1.get(p), Expression.AND);
									}
								}

								for(int q = 0; q < minterm2.size(); q++) {
									intersection.addSubExpressionWithOperator((Expression) minterm2.get(q), Expression.AND);
								}

								// clear for next minterm
								minterm2 = new ArrayList<Expression>();
							}
						}
						// clear for next minterm
						minterm1 = new ArrayList<Expression>();
					}
				}

				// try to simplify intersect expansion
				intersection = simplify(intersection);
			}
		}

		return intersection;
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
    			if(complement == true) {
    				temp.complement();
    			}
    		}

    		return temp;
    }
	
	/**
	 * Minimizes a given expression by determining the sum of products expansion then reducing it to its Disjunctive
	 * Normal Form.
	 * 
	 * @param expr
	 *            The expression to evaluate
	 * @return The evaluated expression
	 */
	public static Expression minimize(Expression expr) {

		if(expr == null) {
			throw(new NullPointerException("Unable to minimize null expression!"));
		}

		return getSumOfProducts(expr, THRESHOLD);

	}


	public static Expression parse(String input) {

		if(input == null || input.trim().length() == 0) {
			return null;
		}

		// normalize "AND"/"OR" to "&&"/"||"
		input = StringUtil.replace(input, "AND", "&&", true, true);
		input = StringUtil.replace(input, "OR", "||", true, true);

		int length = input.length();
		Stack<Character> stack = new Stack<Character>();
		Stack<Expression> expressionStack = new Stack<Expression>();

		int position = 0;
		while(position < length) {

			char current = input.charAt(position);

			// push onto stack until a balanced parenthesis is encountered
			if(current == ')') {

				StringBuffer sb = new StringBuffer();
				while(stack.isEmpty() == false) {

					current = stack.pop();

					if(current == '(') {

						String expr = sb.toString();
						// System.out.println(expr);

						Matcher matcher = SIMPLE_EXPRESSION_PATTERN.matcher(expr);
						if(matcher.find() && matcher.groupCount() == 3) {

							String subject = matcher.group(1);
							String predicate = matcher.group(2);
							String value = matcher.group(3);

							// determine if value is String, Integer, or Double
							Object o = null;
							if(value.startsWith("\"") && value.endsWith("\"")) {

								o = value.substring(1, value.length() - 1);
							}
							else if(value.startsWith("[") && value.endsWith("]")) {
								
								// value is a list
								value = value.substring(1, value.length() - 1);
								
								String[] values = value.split(",");
								
								o = new ArrayList();
								
								for(String s : values) {

								    // NOTE - only strings and integers currently supported for lists, in future may use more introspection to infer type (maybe library for this)
								    if(s.startsWith("\"")) {
                                        s = s.replaceAll("^\"|\"$", "");
                                        ((List)o).add(s);
                                    }
                                    else {
                                        Object v = Integer.valueOf(s.trim());
                                        ((List) o).add(v);
                                    }
								}

							}
							else if("null".equalsIgnoreCase(value)) {
								o = null;
							}
							else if("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
								
								o = Boolean.valueOf(value);
							}
							else {

								// attempt to parse date, then int, then double
								try {
									o = DateUtils.parseDate(value, new String[] { "MM/dd/yy","MM/dd/yy HH:mm:ss", "E MMM d HH:mm:ss z yyyy" });
								}
								catch(Exception e) {

									boolean isInteger = Pattern.matches("^\\d*$", value);
									if(isInteger == true) {
										o = Integer.valueOf(value);
									}
									else {
										o = Double.valueOf(value);
									}
								}
							}

							Expression simple = new Expression(subject, predicate, o);
							// System.out.println(simple);

							expressionStack.push(simple);
							break;
						}
						else {

							reduceExpression(stack, expressionStack);
							break;
						}
					}
					else if(current == '&' || current == '|') {

						// any subexpression should already have been popped off the stack, so if a logical operator
						// is encountered then we know we have a compound expression and should start reducing
						stack.push(current);
						reduceExpression(stack, expressionStack);
						break;

					}
					else {
						sb.insert(0, current);
					}
				}

			}
			else {

				stack.push(current);
			}

			position++;
		}

		// a root compound expression is not required to be surrounded by parentheses so do a final reduction
		if(stack.isEmpty() == false) {

			reduceExpression(stack, expressionStack);
		}

		Expression expression = expressionStack.pop();

		return expression;
	}


	private static void reduceExpression(Stack<Character> stack, Stack<Expression> expressionStack) {

		// try to reduce stack
		if(stack.isEmpty() == false) {

			Expression compound = new Expression();

			// there should always be at least one subexpression in a valid compound
			Expression subExpression = expressionStack.pop();
			compound.addSubExpressionWithOperator(subExpression, null);

			while(stack.isEmpty() == false) {

				char current = stack.pop();

				if(current == '(') {
					break;
				}
				else if(current == ' ') {
					// skip over whitespace surrounding logical operators
					continue;
				}

				if(isLogicalOperator(current)) {

					StringBuffer sb = new StringBuffer();
					sb.append(current).append(stack.pop());

					subExpression = expressionStack.pop();
					compound.addSubExpressionWithOperator(0, subExpression, sb.toString()); // insert at front of expresison
				}
			}

			// System.out.println(compound);
			expressionStack.push(compound);
		}
	}


	private static boolean isComparisonOperator(char c) {

		return(c == '=' || c == '<' || c == '>' || c == '!');
	}


	private static boolean isLogicalOperator(char c) {

		return(c == '&' || c == '|');
	}

}
