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

import personal.wuyi.jibernate.entity.Uri;
import personal.wuyi.jibernate.expression.Expression;
import personal.wuyi.jibernate.expression.ExpressionTransformer;
import personal.wuyi.jibernate.expression.Subject;

/**
 * Uri Expression Transformer.
 * 
 * <p>This class is to transform the expression if the subject is "uri" so 
 * then change "uri" to "id" and get the primary key from URI string.
 * 
 * <p>For example:
 * <pre>
 *     Expression("uri","=","/personal/wuyi/jibernate/entity/Student/27") ==> Expression("id", "=", "27")
 * </pre>
 * 
 * @author  Wuyi Chen
 * @date    09/25/2018
 * @version 1.0
 * @since   1.0
 */
public class UriExpressionTransformer extends ExpressionTransformer {
    @Override
    public Expression transform(Subject subject, String operator, Object value) {
    	Subject newSubject = subject;
        if ("uri".equals(newSubject.getName()) && value != null) {
        	newSubject = new Subject(getAttribute());
        	if (value instanceof String) {
        		value = Uri.parse((String) value);
        	}
        	value = ((Uri) value).getId();
        }

        return super.transform(newSubject, operator, value);
    }

    /**
     * The name of the unique identifier.
     *
     * @return  The name of the unique identifier.
     * 
     * @since   1.0
     */
    protected String getAttribute() {
        return "id";
    }
}
