package personal.wuyi.jibernate.expression;

import org.apache.commons.lang3.time.DateUtils;

import personal.wuyi.jibernate.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * The <code>ExpressionEngine</code> class. Defines a series of static methods for evaluating, minimizing, and
 * manipulating Expressions.
 * 
 * @author Zeffren Zarate
 */
public class ExpressionEngine {


	protected final static String SIMPLE_EXPRESSION_REGEX = "\\[(.*?)\\]\\s*(={2}|[<>]=?|!=|IN|LIKE|CONTAINS)\\s*(.*)";

	private final static int THRESHOLD = 500;
	private final static Pattern simpleExpressionPattern = Pattern.compile( SIMPLE_EXPRESSION_REGEX, Pattern.CASE_INSENSITIVE );

	
	/**
	 * 
	 * @param expr
	 * @return
	 */
	public static boolean evaluate( Expression expr ) {
		
		// FIXME this implementation is sub-optimal, should be revised to use stack reduction implementation to avoid recursion and reduce number of evaluations
		
		// denormalize to flat Sum of Products expansion => (A) + ( B * C * D) + (E * F)	
		List<Expression> minterms = ExpressionEngine.getMinterms( expr );
		
		boolean truth = false;
		for( Expression minterm : minterms ) {
			
			if( minterm.isCompound() ) {
				
				boolean minTruth = true;
				for ( int i = 0; i < minterm.getNumberOfSubExpression(); i++ ) {
					
					Expression child = minterm.getSubExpression( i );
					boolean eval = evaluate( child.getSubject().getValue(), child.getOperator(), child.getValue() );
					minTruth = minTruth && eval;
				}
				
				truth = truth || minTruth;
			}
			else {
				
				boolean eval = evaluate( minterm.getSubject().getValue(), minterm.getOperator(), minterm.getValue() );
				truth = truth || eval;				
			}
		}	
		
		return truth;
	}
	
	
	/*
	 * Compare A to B to determine truth value
	 * ASSERT: A and B are of same type and implement java.lang.Comparable
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static boolean evaluate( Object a, String operator, Object b ) {


		// handle NULL comparisons
		if( a == null ) {

			if( b == null ) {

				// only true for EQUAL
				return Expression.EQUAL.equals( operator );
			}
			else {

				// only true for NOT EQUAL
				return Expression.NOT_EQUAL.equals( operator );
			}
		}
		else {

			if( b == null ) {

				// only true for NOT EQUAL
				return Expression.NOT_EQUAL.equals( operator );
			}
		}

		// check same type
		if( a.getClass().isAssignableFrom( b.getClass() ) == false || b.getClass().isAssignableFrom( a.getClass() ) == false ) {

			throw( new IllegalArgumentException( a.getClass().getName() + " is not comparable to " + b.getClass().getName() ) );
		}

		// check comparable
		if( a instanceof Comparable == false || b instanceof Comparable == false ) {

			throw( new IllegalArgumentException( a.getClass().getName() + " is not comparable to " + b.getClass().getName() ) );
		}

		Comparable p = (Comparable) a;
		Comparable q = (Comparable) b;

		int comparison = p.compareTo( q );


		if( Expression.EQUAL.equals( operator ) ) {

			return comparison == 0;
		}
		else if( Expression.NOT_EQUAL.equals( operator ) ) {

			return comparison != 0;
		}
		else if( Expression.GREATER_THAN.equals( operator ) ) {

			return comparison > 0;
		}
		else if( Expression.GREATER_THAN_EQUAL.equals( operator ) ) {

			return comparison >= 0;
		}
		else if( Expression.LESS_THAN.equals( operator ) ) {

			return comparison < 0;
		}
		else if( Expression.LESS_THAN_EQUAL.equals( operator ) ) {

			return comparison <= 0;
		}
		else {

			throw( new IllegalArgumentException("Comparison " + operator + " not supported.") );
		}
		
	}
	

	/**
	 * Minimizes a given expression by determining the sum of products expansion then reducing it to its Disjunctive
	 * Normal Form.
	 * 
	 * @param expr
	 *            The expression to evaluate
	 * @return The evaluated expression
	 */
	public static Expression minimize( Expression expr ) {

		if( expr == null ) {
			throw(new NullPointerException( "Unable to minimize null expression!" ));
		}

		return getSumOfProducts( expr );

	}


	/**
	 * Minimizes a given expression by determining the sum of products expansion then reducing it to its Disjunctive
	 * Normal Form. Accepts a flag indicating whether Attribute service substitutions should be performed prior to
	 * minimization.
	 * 
	 * @param expr
	 *            The expression to evaluate
	 * @param substitute
	 *            A boolean specifying whether to substitue terms
	 * @return The evaluated expression
	 */
	public static Expression minimize( Expression expr, boolean substitute ) {

		if( expr == null ) {
			throw(new NullPointerException( "Unable to minimize null expression!" ));
		}

		return getSumOfProducts( expr );
	}


	protected static Expression getSumOfProducts( Expression expr ) {

		return getSumOfProducts( expr, THRESHOLD );

	}


	protected static Expression getSumOfProducts( Expression expr, int threshold ) {

		// base case
		if( expr.isCompound() == false ) {
			return expr;
		}
		else {

			Expression sop = null;

			// use a divide-and-conquor strategy to avoid stack overflow
			// for very large expressions
			if( expr.getNumberOfSubExpression() > threshold ) {
				ArrayList<Expression> dividedList = new ArrayList<Expression>();
				int p = 0;
				int q = threshold - 1;

				// Try to break the compound into discrete sub-expressions,
				// and return the union of the minimized results
				while( p <= q && q < expr.getNumberOfSubExpression() ) {
					// get the lookahead operator
					String la = expr.getOperator( q, Expression.SIDE_RIGHT );

					// logical divisions may only occur between disjunctive
					// sub-expressions or when we reach the end of the expression
					if( la == null || la.equals( Expression.OR ) ) {
						// create a sub-expression representing the divided chunk
						Expression divided = new Expression();
						for( int i = p; i <= q; i++ ) {
							divided.add( expr.getSubExpression( i ), expr.getOperator( i, Expression.SIDE_LEFT ) );
						}
						dividedList.add( divided );

						// determine if the end of the expression has been reached
						if( la == null ) {
							break;
						}

						// advance the indices
						p = q + 1;
						q += threshold;
						if( q >= expr.getNumberOfSubExpression() ) {
							q = expr.getNumberOfSubExpression() - 1;
						}
					}
					else {
						// if expression is not logically divisable at the current
						// index then slowly grow until a suitable index is found
						q++;
					}
				}

				// determine if a logical division could be made
				if( dividedList.size() > 1 ) {
					// recursively evaluate divided sub-expressions
					Expression divideAndConquor = new Expression();
					for( int i = 0; i < dividedList.size(); i++ ) {
						Expression subExpression = (Expression) dividedList.get( i );
						Expression normalized = getSumOfProducts( subExpression, THRESHOLD );

						// Add the divided sub-expressions back into the normalized one
						if( normalized.isCompound() == false ) {
							divideAndConquor.add( normalized, Expression.OR );
						}
						else {
							// concatenate with the main expression since they
							// are logically the same expression and not really
							// a nested sub-expression
							divideAndConquor.addAll( normalized, Expression.OR );
						}
					}
					sop = divideAndConquor;
				}
				else {
					// shrink the threshold until a divisable size can be found
					if( threshold > 1 ) {
						threshold = threshold / 2;
						sop = getSumOfProducts( expr, threshold );
					}
					else {
						// FIXME: deal with all AND expression
					}
				}

			}
			else {
				sop = _getSumOfProducts( expr );
			}
			return sop;
		}
	}


	/**
	 * Takes an expression and performs the logical sum of products expansion,
	 * 
	 * Given an expression of the form:
	 * 
	 * ((A+B)*(C*(D+E)))
	 * 
	 * will return sum of products expansion
	 * 
	 * ACD + ACE + BCD + BCE
	 * 
	 * @param expr
	 *            The expression to evaluate
	 * 
	 * @return An Expression excapsulating the sum of products expansion
	 */
	private static Expression _getSumOfProducts( Expression expr ) {

		Stack exprStack = new Stack(); // the expression tree stack
		Stack pdaStack = new Stack(); // pda stack
		Stack operStack = new Stack(); // operator stack

		// push initial expression onto the stack
		exprStack.push( expr );
		exprStack.push( new Integer( 0 ) );

		// push compound complement
		if( expr.isComplement() ) {
			operStack.push( "!" );
		}

		if( expr.isCompound() ) {
			operStack.push( "_" );
		}
		
		// walk the parse tree
		while( !exprStack.isEmpty() ) {
			boolean descend = false;

			// store current expr index
			int itr = ((Integer) exprStack.pop()).intValue();

			// process stack expression
			expr = (Expression) exprStack.pop();

			// break out of loop if descending into child expression, otherwise
			// continue left to right evaluation parse
			for( int i = itr; !descend && i < expr.getNumberOfSubExpression(); i++ ) {
				Expression child = expr.getSubExpression( i );

				// attempt to reduce nested parens
				if( child.isCompound() ) {
					child = simmer( child );
				}

				String op = (i > 0 ? expr.getOperator( i, Expression.SIDE_LEFT ) : null);

				String la = (i == (expr.getNumberOfSubExpression() - 1) ? null : expr.getOperator( i, Expression.SIDE_RIGHT ));

				// DEBUG
				/*
				 * System.out.println("\n op: " + op); System.out.println(" child: " + child);
				 * System.out.println(" la: " + la + "\n"); System.out.println(operStack); System.out.println(pdaStack);
				 */

				if( child.isCompound() == false ) {
					if( la != null ) {
						// give AND precedence
						if( op != null && op.equals( Expression.AND ) ) {
							// operStack.pop();
							Expression popExpr = (Expression) pdaStack.pop();

							pdaStack.push( intersection( popExpr, child ) );
						}
						else {
							if( op != null )
								operStack.push( op );
							pdaStack.push( child );
						}
					}
					else {
						if( op != null )
							operStack.push( op );

						pdaStack.push( child );

						reduce( operStack, pdaStack, false );
					}
				}
				else {
					// push the operator and stack separator
					// to indicate compound expression reduction
					if( op != null )
						operStack.push( op );

					// push compound complement
					if( child.isComplement() ) {
						operStack.push( "!" );
					}

					// push stack seperator
					operStack.push( "_" );

					// save parent position
					exprStack.push( expr );
					exprStack.push( new Integer( i + 1 ) );

					// make the child the current expr
					expr = child;

					// push child onto minimize stack
					exprStack.push( child );
					exprStack.push( new Integer( 0 ) );

					// descend branch to minimize child
					descend = true;
				}
			}
		}

		// done traversing so start popping
		reduce( operStack, pdaStack, true );
		Expression sop = (Expression) pdaStack.pop();

		if( sop.isCompound() ) {
			sop = simplify( sop );

			if( sop.getNumberOfSubExpression() == 1 ) {
				Expression temp = sop.getSubExpression( 0 );

				if( sop.isComplement() ) {
					temp.complement();
				}
				sop = temp;
			}
		}

		// minimzed sum of products expression
		return sop;
	}


	/**
	 * Do the pda stack reduction. Applies shift/reduction rules.
	 */
	private static void reduce( Stack operStack, Stack pdaStack, boolean reduceAll ) {

		while( !operStack.isEmpty() ) {
			String operator = (String) operStack.pop();

			if( operator.equals( "_" ) ) {
				// Hit the stack separator so try to see if we can reduce any ANDS,
				// otherwise break out of reduction loop
				if( reduceAll == false ) {
					while( !operStack.isEmpty() &&
					// !((String)operStack.peek()).equals(Expression.OR) &&
							!((String) operStack.peek()).equals( "_" ) ) {

						String op = (String) operStack.pop();

						if( op.equals( "!" ) ) {
							Expression complementExpr = (Expression) pdaStack.pop();

							if( complementExpr.isCompound() ) {
								complementExpr.complement( true );

								if( complementExpr.getNumberOfSubExpression() == 1 ) {
									complementExpr = simmer( complementExpr );
								}
								else {
									// complement operation turns SOP expr into POS
									// !(AB + C + !DE) <==> (!A + !B) * !C * (D + !E)
									// so we need to multiply out
									// !A!CD + !A!C!E + !B!CD + !B!C!E
									Expression temp = complementExpr.getSubExpression( 0 );
									for( int i = 1; i < complementExpr.getNumberOfSubExpression(); i++ ) {
										Expression blah = complementExpr.getSubExpression( i );
										temp = intersection( temp, blah );
									}
									complementExpr = temp;
								}
							}
							else {
								Expression temp = (Expression) complementExpr.clone();
								complementExpr = temp;
								complementExpr.complement();
							}
							
							pdaStack.push( complementExpr );
						}
						else if( op.equals( Expression.OR ) ) {
							Expression expr2 = (Expression) pdaStack.pop();
							Expression expr1 = (Expression) pdaStack.pop();

							pdaStack.push( union( expr1, expr2 ) );
						}
						else {
							Expression expr2 = (Expression) pdaStack.pop();
							Expression expr1 = (Expression) pdaStack.pop();

							pdaStack.push( intersection( expr1, expr2 ) );
						}
					}

					break;
				}
			}
			else if( operator.equals( "!" ) ) {
				Expression complementExpr = (Expression) pdaStack.pop();
				if( complementExpr.isCompound() ) {
					complementExpr.complement( true );
					if( complementExpr.getNumberOfSubExpression() == 1 ) {
						complementExpr = simmer( complementExpr );
					}
					else {
						// complement operation turns SOP expr into POS
						// !(AB + C + !DE) <==> (!A + !B) * !C * (D + !E)
						// so we need to multiply out
						// !A!CD + !A!C!E + !B!CD + !B!C!E
						Expression temp = complementExpr.getSubExpression( 0 );
						for( int i = 1; i < complementExpr.getNumberOfSubExpression(); i++ ) {
							Expression blah = complementExpr.getSubExpression( i );
							temp = intersection( temp, blah );
						}
						complementExpr = temp;
					}
				}
				else {
					Expression temp = (Expression) complementExpr.clone();
					complementExpr = temp;
					complementExpr.complement();
				}

				pdaStack.push( complementExpr );
			}
			else {
				Expression expr2 = (Expression) pdaStack.pop();
				Expression expr1 = (Expression) pdaStack.pop();

				if( operator.equals( Expression.OR ) ) {
					pdaStack.push( union( expr1, expr2 ) );
				}
				else {
					pdaStack.push( intersection( expr1, expr2 ) );
				}
			}
		}
	}


	/**
	 * Takes an expanded expression and attempts to simplify. ASSERTION: The expression is compound and expanded (flat)
	 */
	private static Expression simplify( Expression expr ) {

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
		for( int i = 0; i < minimized.getNumberOfSubExpression(); i++ ) {

			Expression child = minimized.getSubExpression( i );
			String la = (i + 1 == minimized.getNumberOfSubExpression() ? null : minimized.getOperator( i, Expression.SIDE_RIGHT ));

			if( !minterm.contains( child ) )
				minterm.add( child );

			// dump when the next or'd term or the end of the expression is reached
			if( la == null || !la.equals( Expression.AND ) ) {

				mintermList.add( minterm );
				minterm = new ArrayList<Expression>();
			}
		}

		// simplify by removing redundant minterms
		int p = 0, q = 0;
		while( p < mintermList.size() ) {

			List<Expression> current = mintermList.get( p );

			while( q < mintermList.size() ) {

				if( p != q ) {

					List<Expression> next = mintermList.get( q );

					if( current.containsAll( next ) ) {

						mintermList.remove( p );
						q = 0;
						break;
					}

					if( next.containsAll( current ) ) {

						mintermList.remove( q );

						if( q < p )
							p--;

					}
					else {

						q++;
					}

				}
				else {

					q++;
				}

				if( q == mintermList.size() ) {
					p++;
				}
			}
			q = 0;
		}

		minimized = new Expression();

		// recontruct the new minimized expression
		for( int m = 0; m < mintermList.size(); m++ ) {

			minterm = mintermList.get( m );

			for( int n = 0; n < minterm.size(); n++ ) {

				if( n == 0 ) {

					minimized.add( minterm.get( n ), Expression.OR );

				}
				else {

					minimized.add( minterm.get( n ), Expression.AND );
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
	private static boolean doUnaryOperation( Expression expr, boolean evaluation ) {

		if( expr.isComplement() ) {

			return(!evaluation);

		}
		else {

			return(evaluation);
		}
	}


	/**
	 * performs union of two minimized expressions
	 */
	private static Expression union( Expression e1, Expression e2 ) {

		Expression union = null;
		if( e1.isCompound() == false ) {

			if( e2.isCompound() == false ) {

				union = new Expression();
				union.add( e1, null );
				union.add( e2, Expression.OR );
			}
			else {

				union = e2;
				union.add( 0, e1, Expression.OR );
			}
		}
		else {

			if( e2.isCompound() == false ) {

				union = e1;
				union.add( e2, Expression.OR );
			}
			else {

				union = e1;
				union.addAll( e2, Expression.OR );
			}
		}

		return union;
	}


	/**
	 * performs intersection of two minimized expressions
	 */
	private static Expression intersection( Expression e1, Expression e2 ) {

		Expression intersection = null;
		if( e1.isCompound() == false ) {

			if( e2.isCompound() == false ) {

				intersection = new Expression();
				intersection.add( e1, null );
				intersection.add( e2, Expression.AND );

			}
			else {

				intersection = new Expression();

				for( int i = 0; i < e2.getNumberOfSubExpression(); i++ ) {

					Expression child = (Expression) e2.getSubExpression( i );
					String op = (i == 0 ? null : e2.getOperator( i, Expression.SIDE_LEFT ));

					if( op == null || op.equals( Expression.OR ) ) {
						intersection.add( e1, Expression.OR );
					}

					// simplify redundant terms
					// A * AB ==> AB
					if( !child.equals( e1 ) ) {
						intersection.add( child, Expression.AND );
					}
				}
			}

		}
		else {

			if( e2.isCompound() == false ) {

				intersection = new Expression();

				boolean matched = false;
				for( int i = 0; i < e1.getNumberOfSubExpression(); i++ ) {

					Expression child = e1.getSubExpression( i );
					String op = (i == 0 ? null : e1.getOperator( i, Expression.SIDE_LEFT ));
					String la = (i + 1 == e1.getNumberOfSubExpression() ? null : e1.getOperator( i, Expression.SIDE_RIGHT ));

					intersection.add( child, op );

					// simplify redundant terms
					// A * AB ==> AB
					if( child.equals( e2 ) ) {
						matched = true;
					}

					if( la == null || la.equals( Expression.OR ) ) {
						if( matched == false ) {
							intersection.add( e2, Expression.AND );
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
				for( int i = 0; i < e1.getNumberOfSubExpression(); i++ ) {
					Expression alpha = e1.getSubExpression( i );
					String op1 = (i + 1 == e1.getNumberOfSubExpression() ? null : e1.getOperator( i, Expression.SIDE_RIGHT ));

					minterm1.add( alpha );

					if( null == op1 || op1.equals( Expression.OR ) ) {
						for( int j = 0; j < e2.getNumberOfSubExpression(); j++ ) {
							Expression beta = e2.getSubExpression( j );
							String op2 = (j + 1 == e2.getNumberOfSubExpression() ? null : e2.getOperator( j, Expression.SIDE_RIGHT ));

							minterm2.add( beta );

							if( null == op2 || op2.equals( Expression.OR ) ) {
								// create "flat" intersection of "ANDED" minterms so:
								// (A*B + C)*(D*E + F*G) = (A*B*D*E + A*B*F*G + C*D*E + C*F*G)
								for( int p = 0; p < minterm1.size(); p++ ) {
									// "OR" in the fist "ANDED" minterm
									if( p == 0 ) {
										intersection.add( (Expression) minterm1.get( p ), Expression.OR );
									}
									else {
										intersection.add( (Expression) minterm1.get( p ), Expression.AND );
									}
								}

								for( int q = 0; q < minterm2.size(); q++ ) {
									intersection.add( (Expression) minterm2.get( q ), Expression.AND );
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
				intersection = simplify( intersection );
			}
		}

		return intersection;
	}


	/**
	 * Strip nested parens ((((E)))) --> E
	 */
	private static Expression simmer( Expression expr ) {

		if( expr.isCompound() == false ) {
			return expr;
		}

		Expression simmered = expr;

		while( simmered.getNumberOfSubExpression() == 1 ) {
			// preserve the complement
			boolean complement = simmered.isComplement();

			simmered = simmered.getSubExpression( 0 );

			if( complement == true ) {
				simmered.complement();
			}
		}

		return simmered;
	}



	/**
	 * Generates a list of sum-of-product minterms for a given compound expression. Method will minimize the input
	 * expression to generate the normalized SOP expression whose minterms are the "anded" sub-expressions separated by
	 * "or" operators.
	 * 
	 * For example, the mimized expression:
	 * 
	 * a * b + c * d * e + f
	 * 
	 * produces the set of minterms:
	 * 
	 * {ab}, {cde}, {f}
	 * 
	 * @param expression
	 * @return List of minterm sub-expressions.
	 */
	public static List<Expression> getMinterms( Expression expression ) {

		// make sure expression is valid
		if( expression == null ) {
			return null;
		}

		Expression minimized = minimize( expression );

		List<Expression> mintermList = new ArrayList<Expression>();

		// handle simple expression
		if( minimized.isCompound() == false ) {
			Expression minterm = new Expression();
			minterm.add( minimized, null );
			mintermList.add( minterm );

			return mintermList;
		}

		Expression minterm = new Expression();

		// build list of minterm expressions
		for( int i = 0; i < minimized.getNumberOfSubExpression(); i++ ) {
			Expression child = minimized.getSubExpression( i );

			String la = ((i + 1) == minimized.getNumberOfSubExpression()) ? null : minimized.getOperator( i, Expression.SIDE_RIGHT );

			minterm.add( child, Expression.AND );

			// dump when the next or'd term or the end of the expression is reached
			if( la == null || la.equals( Expression.OR ) ) {
				mintermList.add( minterm );
				minterm = new Expression();
			}
		}

		// done
		return mintermList;
	}


	public static Expression parse( String input ) {

		if( input == null || input.trim().length() == 0 ) {
			return null;
		}

		// normalize "AND"/"OR" to "&&"/"||"
		input = StringUtil.replace( input, "AND", "&&", true, true );
		input = StringUtil.replace( input, "OR", "||", true, true );

		int length = input.length();
		Stack<Character> stack = new Stack<Character>();
		Stack<Expression> expressionStack = new Stack<Expression>();

		int position = 0;
		while( position < length ) {

			char current = input.charAt( position );

			// push onto stack until a balanced parenthesis is encountered
			if( current == ')' ) {

				StringBuffer sb = new StringBuffer();
				while( stack.isEmpty() == false ) {

					current = stack.pop();

					if( current == '(' ) {

						String expr = sb.toString();
						// System.out.println( expr );

						Matcher matcher = simpleExpressionPattern.matcher( expr );
						if( matcher.find() && matcher.groupCount() == 3 ) {

							String subject = matcher.group( 1 );
							String predicate = matcher.group( 2 );
							String value = matcher.group( 3 );

							// determine if value is String, Integer, or Double
							Object o = null;
							if( value.startsWith( "\"" ) && value.endsWith( "\"" ) ) {

								o = value.substring( 1, value.length() - 1 );
							}
							else if( value.startsWith("[") && value.endsWith("]") ) {
								
								// value is a list
								value = value.substring( 1, value.length() - 1 );
								
								String[] values = value.split( "," );
								
								o = new ArrayList();
								
								for( String s : values ) {

								    // NOTE - only strings and integers currently supported for lists, in future may use more introspection to infer type (maybe library for this)
								    if( s.startsWith("\"") ) {
                                        s = s.replaceAll("^\"|\"$", "");
                                        ((List)o).add( s );
                                    }
                                    else {
                                        Object v = Integer.valueOf( s.trim() );
                                        ((List) o).add( v );
                                    }
								}

							}
							else if( "null".equalsIgnoreCase( value ) ) {
								o = null;
							}
							else if( "true".equalsIgnoreCase( value ) || "false".equalsIgnoreCase( value ) ) {
								
								o = Boolean.valueOf( value );
							}
							else {

								// attempt to parse date, then int, then double
								try {
									o = DateUtils.parseDate( value, new String[] { "MM/dd/yy","MM/dd/yy HH:mm:ss", "E MMM d HH:mm:ss z yyyy" } );
								}
								catch( Exception e ) {

									boolean isInteger = Pattern.matches("^\\d*$", value );
									if( isInteger == true ) {
										o = Integer.valueOf( value );
									}
									else {
										o = Double.valueOf( value );
									}
								}
							}

							Expression simple = new Expression( subject, predicate, o );
							// System.out.println( simple );

							expressionStack.push( simple );
							break;
						}
						else {

							reduceExpression( stack, expressionStack );
							break;
						}
					}
					else if( current == '&' || current == '|' ) {

						// any subexpression should already have been popped off the stack, so if a logical operator
						// is encountered then we know we have a compound expression and should start reducing
						stack.push( current );
						reduceExpression( stack, expressionStack );
						break;

					}
					else {
						sb.insert( 0, current );
					}
				}

			}
			else {

				stack.push( current );
			}

			position++;
		}

		// a root compound expression is not required to be surrounded by parentheses so do a final reduction
		if( stack.isEmpty() == false ) {

			reduceExpression( stack, expressionStack );
		}

		Expression expression = expressionStack.pop();

		return expression;
	}


	private static void reduceExpression( Stack<Character> stack, Stack<Expression> expressionStack ) {

		// try to reduce stack
		if( stack.isEmpty() == false ) {

			Expression compound = new Expression();

			// there should always be at least one subexpression in a valid compound
			Expression subExpression = expressionStack.pop();
			compound.add( subExpression, null );

			while( stack.isEmpty() == false ) {

				char current = stack.pop();

				if( current == '(' ) {
					break;
				}
				else if( current == ' ' ) {
					// skip over whitespace surrounding logical operators
					continue;
				}

				if( isLogicalOperator( current ) ) {

					StringBuffer sb = new StringBuffer();
					sb.append( current ).append( stack.pop() );

					subExpression = expressionStack.pop();
					compound.add( 0, subExpression, sb.toString() ); // insert at front of expresison
				}
			}

			// System.out.println( compound );
			expressionStack.push( compound );
		}
	}


	private static boolean isComparisonOperator( char c ) {

		return(c == '=' || c == '<' || c == '>' || c == '!');
	}


	private static boolean isLogicalOperator( char c ) {

		return(c == '&' || c == '|');
	}

}
