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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import personal.wuyi.jibernate.expression.Expression;
import personal.wuyi.jibernate.expression.Subject;

/**
 * The test class for {@code UriExpressionTransformer}.
 * 
 * @author  Wuyi Chen
 * @date    02/12/2018
 * @version 1.1
 * @since   1.1
 */
public class UriExpressionTransformerTest {
	private UriExpressionTransformer transformer;
	
	@Before
	public void initialize() {
		transformer = new UriExpressionTransformer();
	}
	
	@Test
	public void transformTest() {
		Assert.assertEquals(new Expression("id", Expression.EQUAL, 27), transformer.transform(new Subject("uri"), Expression.EQUAL, "/personal/wuyi/jibernate/entity/Student/27"));
	}
	
	@Test
	public void getAttributeTest() {
		Assert.assertEquals("id", transformer.getAttribute());
	}
}
