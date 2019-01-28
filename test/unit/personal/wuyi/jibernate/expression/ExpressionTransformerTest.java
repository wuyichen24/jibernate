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

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test class for {@code ExpressionTransformer}.
 *
 * @author  Wuyi Chen
 * @date    01/25/2019
 * @version 1.1
 * @since   1.1
 */
public class ExpressionTransformerTest {
	private Expression sinAExpr          = new Expression("firstName", Expression.EQUAL, "John");
	private Expression com2AndExpr       = new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23);
	private Expression comMultiLevelExpr = new Expression(new Expression("firstName", Expression.EQUAL, "John").and("age", Expression.EQUAL, 23))
				.or(new Expression("firstName", Expression.EQUAL, "Mary").and("age", Expression.EQUAL, 24).and("score", Expression.EQUAL, 99))
				.or(new Expression("firstName", Expression.EQUAL, "Tony").and("age", Expression.EQUAL, 25).and("lastName", Expression.EQUAL, "Lee"));
	
	@Test
	public void transformTest() {
		ExpressionTransformer et = Mockito.mock(ExpressionTransformer.class, Mockito.CALLS_REAL_METHODS);
		
		Assert.assertNull(et.transform(null));
		Assert.assertEquals(sinAExpr,          et.transform(sinAExpr));
		Assert.assertEquals(sinAExpr,          et.transform(sinAExpr, "Tony"));
		Assert.assertEquals(com2AndExpr,       et.transform(com2AndExpr));
		Assert.assertEquals(com2AndExpr,       et.transform(com2AndExpr, "Tony"));
		Assert.assertEquals(comMultiLevelExpr, et.transform(comMultiLevelExpr));
		Assert.assertEquals(comMultiLevelExpr, et.transform(comMultiLevelExpr, "Tony"));
	}
}
