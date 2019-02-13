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

package personal.wuyi.jibernate.transformer;

import personal.wuyi.jibernate.expression.Expression;
import personal.wuyi.jibernate.expression.ExpressionTransformer;
import personal.wuyi.jibernate.expression.Subject;

/**
 * Search Expression Transformer.
 *
 * <p>This class is to transform the expression if the operator is using 
 * "START_WITH", "END_WITH" or "CONTAINS", they will be replaced by "LIKE" 
 * with %. For example:
 * <pre>
 *   START_WITH 'ABC' ==> LIKE 'ABC%'
 *   END_WITH 'ABC'   ==> LIKE '%ABC'
 *   CONTAINS 'ABC'   ==> LIKE '%ABC%'
 * </pre>
 * 
 * @author  Wuyi Chen
 * @date    09/26/2018
 * @version 1.1
 * @since   1.0
 */
public class SearchExpressionTransformer extends ExpressionTransformer {
    @Override
    public Expression transform(Subject subject, String operator, Object value) {
    	switch (operator) {
        	case Expression.STARTS_WITH : return super.transform(subject, "LIKE", value + "%");
        	case Expression.ENDS_WITH   : return super.transform(subject, "LIKE", "%" + value);
        	case Expression.CONTAINS    : return super.transform(subject, "LIKE", "%" + value + "%");
        	default: return super.transform(subject, operator, value);
    	}
    }
}
