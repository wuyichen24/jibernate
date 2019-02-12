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

import org.junit.Before;
import org.junit.Test;
import org.testng.Assert;

import personal.wuyi.jibernate.expression.Expression;
import personal.wuyi.jibernate.expression.Subject;

/**
 * The test class for {@code SearchExpressionTransformer}.
 * 
 * @author  Wuyi Chen
 * @date    02/12/2018
 * @version 1.1
 * @since   1.1
 */
public class SearchExpressionTransformerTest {
	private SearchExpressionTransformer transformer;
	
	@Before
	public void initialize() {
		transformer = new SearchExpressionTransformer();
	}
	
	@Test
	public void transformTest() {
	    // test equal
		Assert.assertEquals(new Expression("firstName", Expression.EQUAL, "John"), transformer.transform(new Subject("firstName"), Expression.EQUAL, "John"));
		
		// test start_with
		Assert.assertEquals(new Expression("firstName", "LIKE", "John%"), transformer.transform(new Subject("firstName"), Expression.STARTS_WITH, "John"));
		
		// test end_with
		Assert.assertEquals(new Expression("firstName", "LIKE", "%John"), transformer.transform(new Subject("firstName"), Expression.ENDS_WITH, "John"));
		
		// test contains
		Assert.assertEquals(new Expression("firstName", "LIKE", "%John%"), transformer.transform(new Subject("firstName"), Expression.CONTAINS, "John"));
	}
}
