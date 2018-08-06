package personal.wuyi.jibernate.io.persist.expression;

import personal.wuyi.jibernate.io.persist.transformer.AbstractTransformer;

/**
 * AbstractExpressionTransformer.java
 *
 * @author zzarate
 */
public abstract class ExpressionTransformer extends AbstractTransformer<Expression,Expression> {

	
	/**
	 * Transform the argument expression.
	 * 
	 * Default implementation will walk expression hierarchy and attempt to individually transform "simple" expressions,
	 * resulting in a new/cloned expression, and leaving the original object unmodified.
	 * 
	 * @param source 
	 * @return The transformed expression
	 * 
	 */
	@Override
	public Expression transform( Expression expression, Object... context ) {

		if( expression == null ) {
			return null;
		}

		if ( expression.isCompound() == false ) {

			Expression transformed = transform( expression.getSubject(), expression.getPredicate(), expression.getValue() );
			return super.transform( transformed, context );
		}
		else {
			
			Expression transformed = new Expression();

			for ( int i = 0; i < expression.size(); i++ ) {
				
				Expression child = expression.getSubExpression( i );
				child = transform( child );

				if ( child != null ) {
					
					if( Expression.AND.equals( expression.getOperator( i ) ) ) {
						transformed.and( child );
					}
					else {
						transformed.or( child );
					}
				}
			}
			
			if ( transformed.size() == 0 ) {
				return null;
			}
			
			return super.transform( transformed );
		}
	
	}
	
	
	/**
	 * Selective transformation of expression.
	 * 
	 * Default method will create new simple expression, effectively resulting in a clone.
	 * 
	 * ASSERT: The expression argument is a "simple" expression
	 * 
	 * @param subject
	 * @param predicate
	 * @param value
	 * @return
	 */
	public Expression transform( Subject subject, String predicate, Object value ) {
		
		return( new Expression( subject, predicate, value ) );
	}
}
