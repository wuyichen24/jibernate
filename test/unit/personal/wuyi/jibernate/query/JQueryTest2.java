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

package personal.wuyi.jibernate.query;

import org.junit.Test;

import junit.framework.Assert;
import personal.wuyi.jibernate.entity.Student;
import personal.wuyi.jibernate.expression.Expression;

/**
 * The test class for {@code Query}.
 * 
 * <p>This class is without DB connection.
 * 
 * @author  Wuyi Chen
 * @date    01/28/2018
 * @version 1.1
 * @since   1.1
 */
public class JQueryTest2 {
	@Test
	public void setPersistedClassTest() {
		JQuery<Student> jq = new JQuery<>(null);
		jq.setPersistedClass(Student.class);
		Assert.assertEquals(Student.class, jq.getPersistedClass());
	}
	
	@Test
	public void setCaseSensitiveTest() {
		JQuery<Student> jq = new JQuery<>(Student.class);
		Assert.assertTrue(jq.isCaseSensitive());    // default is case-sensitive
		jq.setCaseSensitive(false);
		Assert.assertFalse(jq.isCaseSensitive());
	}
	
	@Test
	public void setDistinctTest() {
		JQuery<Student> jq = new JQuery<>(Student.class);
		Assert.assertFalse(jq.isDistinct());   // default is NOT distinct
		jq.setDistinct(true);
		Assert.assertTrue(jq.isDistinct());
	}
	
	@Test
	public void setHistoryTest() {
		JQuery<Student> jq = new JQuery<>(Student.class);
		Assert.assertFalse(jq.isHistory());  // default is history false
		jq.setHistory(true);
		Assert.assertTrue(jq.isHistory());
	}
	
	@Test
	public void setCriteriaTest() {
		JQuery<Student> jq = new JQuery<>(Student.class);
		Assert.assertEquals(new Expression("firstName", Expression.EQUAL, "John"), jq.setCriteria("firstName", Expression.EQUAL, "John"));
	}
	
	@Test
	public void toStringTest() {
		JQuery<Student> jq = new JQuery<>(Student.class);
		jq.setCriteria("firstName", Expression.EQUAL, "John");
		Assert.assertEquals("", jq.toString());
	}
}
