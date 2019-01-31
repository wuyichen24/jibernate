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
		Assert.assertEquals("{class:Student,criteria:([firstName]==\"John\"),sort:null,offset:null,limit:null,caseSensitive:true,distinct:false,history:false}", jq.toString());
	}
	
	@Test
	public void equals () {
		// some special cases
		JQuery<Student> jq = new JQuery<>(Student.class);
		Assert.assertFalse(jq.equals(null));
		Assert.assertFalse(jq.equals("ABC"));
		
		// test class equality
		JQuery<Student> jq1a = new JQuery<>(Student.class);
		JQuery<Student> jq1b = new JQuery<>(Student.class);
		JQuery<Student> jq1c = new JQuery<>(null);
		Assert.assertFalse(jq1a.equals(jq1c));
		Assert.assertFalse(jq1c.equals(jq1a));
		Assert.assertTrue(jq1a.equals(jq1b));
		
		// test criteria equality
		JQuery<Student> jq2a = new JQuery<>(Student.class);
		JQuery<Student> jq2b = new JQuery<>(Student.class);
		JQuery<Student> jq2c = new JQuery<>(Student.class);
		JQuery<Student> jq2d = new JQuery<>(Student.class);
		jq2a.setCriteria("firstName", Expression.EQUAL, "John");
		jq2b.setCriteria("firstName", Expression.EQUAL, "John");
		jq2c.setCriteria("firstName", Expression.EQUAL, "Johnny");
		jq2d.setCriteria(null);
		Assert.assertTrue(jq2a.equals(jq2b));
		Assert.assertFalse(jq2a.equals(jq2c));
		Assert.assertFalse(jq2c.equals(jq2a));
		Assert.assertFalse(jq2a.equals(jq2d));
		Assert.assertFalse(jq2d.equals(jq2a));
		
		// test sort
		JQuery<Student> jq3a = new JQuery<>(Student.class);
		JQuery<Student> jq3b = new JQuery<>(Student.class);
		JQuery<Student> jq3c = new JQuery<>(Student.class);
		JQuery<Student> jq3d = new JQuery<>(Student.class);
		jq3a.setSort(new Sort("gpa+"));
		jq3b.setSort(new Sort("gpa+"));
		jq3c.setSort(new Sort("gpa-"));
		jq3d.setSort(new Sort(null));
		Assert.assertTrue(jq3a.equals(jq3b));
		Assert.assertFalse(jq3a.equals(jq3c));
		Assert.assertFalse(jq3c.equals(jq3a));
		Assert.assertFalse(jq3a.equals(jq3d));
		Assert.assertFalse(jq3d.equals(jq3a));
		
		// test offset
		JQuery<Student> jq4a = new JQuery<>(Student.class);
		JQuery<Student> jq4b = new JQuery<>(Student.class);
		JQuery<Student> jq4c = new JQuery<>(Student.class);
		JQuery<Student> jq4d = new JQuery<>(Student.class);
		jq4a.setOffset(10);
		jq4b.setOffset(10);
		jq4c.setOffset(3);
		jq4d.setOffset(null);
		Assert.assertTrue(jq4a.equals(jq4b));
		Assert.assertFalse(jq4a.equals(jq4c));
		Assert.assertFalse(jq4c.equals(jq4a));
		Assert.assertFalse(jq4a.equals(jq4d));
		Assert.assertFalse(jq4d.equals(jq4a));
	
		// test limit
		JQuery<Student> jq5a = new JQuery<>(Student.class);
		JQuery<Student> jq5b = new JQuery<>(Student.class);
		JQuery<Student> jq5c = new JQuery<>(Student.class);
		jq5a.setLimit(10);
		jq5b.setLimit(10);
		jq5c.setLimit(3);
		Assert.assertTrue(jq5a.equals(jq5b));
		Assert.assertFalse(jq5a.equals(jq5c));
		Assert.assertFalse(jq5c.equals(jq5a));
		
		// test case-sensitive
		JQuery<Student> jq6a = new JQuery<>(Student.class);
		JQuery<Student> jq6b = new JQuery<>(Student.class);
		JQuery<Student> jq6c = new JQuery<>(Student.class);
		jq6a.setCaseSensitive(true);
		jq6b.setCaseSensitive(true);
		jq6c.setCaseSensitive(false);
		Assert.assertTrue(jq6a.equals(jq6b));
		Assert.assertFalse(jq6a.equals(jq6c));
		Assert.assertFalse(jq6c.equals(jq6a));
		
		// test distinct
		JQuery<Student> jq7a = new JQuery<>(Student.class);
		JQuery<Student> jq7b = new JQuery<>(Student.class);
		JQuery<Student> jq7c = new JQuery<>(Student.class);
		jq7a.setDistinct(true);
		jq7b.setDistinct(true);
		jq7c.setDistinct(false);
		Assert.assertTrue(jq7a.equals(jq7b));
		Assert.assertFalse(jq7a.equals(jq7c));
		Assert.assertFalse(jq7c.equals(jq7a));
		
		// test history
		JQuery<Student> jq8a = new JQuery<>(Student.class);
		JQuery<Student> jq8b = new JQuery<>(Student.class);
		JQuery<Student> jq8c = new JQuery<>(Student.class);
		jq8a.setDistinct(true);
		jq8b.setDistinct(true);
		jq8c.setDistinct(false);
		Assert.assertTrue(jq8a.equals(jq8b));
		Assert.assertFalse(jq8a.equals(jq8c));
		Assert.assertFalse(jq8c.equals(jq8a));
	}
}
