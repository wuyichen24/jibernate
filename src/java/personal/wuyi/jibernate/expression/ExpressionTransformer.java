/*
 * Copyright 2018 Wuyi Chen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package personal.wuyi.jibernate.expression;

import personal.wuyi.jibernate.transformer.AbstractTransformer;

/**
 * The abstract class for transforming an expression.
 *
 * @author  Wuyi Chen
 * @date    09/17/2018
 * @version 1.0
 * @since   1.0
 */
public abstract class ExpressionTransformer extends AbstractTransformer<Expression,Expression> {
	@Override
	public Expression transform(Expression expression, Object... context) {
		if(expression == null) {
			return null;
		}

		if (!expression.isCompound()) {
			Expression transformed = transform(expression.getSubject(), expression.getOperator(), expression.getValue());
			return super.transform(transformed, context);
		} else {
			Expression transformed = new Expression();
			for (int i = 0; i < expression.getNumberOfSubExpression(); i++) {
				
				Expression subExpr = expression.getSubExpression(i);
				subExpr = transform(subExpr);

				if (subExpr != null) {
					if(Expression.AND.equals(expression.getOperator(i))) {
						transformed.and(subExpr);
					} else {
						transformed.or(subExpr);
					}
				}
			}
			
			if (transformed.getNumberOfSubExpression() == 0) {
				return null;
			}
			
			return super.transform(transformed);
		}
	}
	
	
	/**
	 * Clone a new simple expression.
	 * 
	 * @param  subject
	 *         The subject of an expression.
	 *         
	 * @param  operator
	 *         The operator of an expression.
	 * 
	 * @param  value
	 *         The value of an expression.
	 *         
	 * @return  The cloned expression.
	 * 
     * @since   1.0 
	 */
	public Expression transform(Subject subject, String operator, Object value) {
		return(new Expression(subject, operator, value));
	}
}
