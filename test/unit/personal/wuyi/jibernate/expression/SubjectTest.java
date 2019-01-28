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

import org.junit.Test;

import junit.framework.Assert;

/**
 * Test class for {@code Subject}.
 * 
 * @author  Wuyi Chen
 * @date    09/18/2018
 * @version 1.1
 * @since   1.0
 */
public class SubjectTest {
	@Test
	public void constructorTest() {
		Subject sub1 = new Subject();
		Assert.assertNull(sub1.getName());
		Assert.assertNull(sub1.getValue());
		
		Subject sub2 = new Subject("Young");
		Assert.assertEquals("Young", sub2.getName());
		Assert.assertNull(sub2.getValue());
		
		Subject sub3 = new Subject("Young", "Lisa");
		Assert.assertEquals("Young", sub3.getName());
		Assert.assertEquals("Lisa", sub3.getValue());
	}
	
	@Test
	public void cloneTest() {
		Subject oldSubject    = new Subject("first name", "John");
		Subject clonedSubject = (Subject) oldSubject.clone();
		clonedSubject.setName("last name");
		clonedSubject.setValue("Mary");	
		Assert.assertEquals("first name", oldSubject.getName());
		Assert.assertEquals("John",       oldSubject.getValue());
	}
	
	@Test
	public void equalsTest() {
		// some special cases
		Assert.assertFalse(new Subject("first name", "John").equals(null));
		Assert.assertFalse(new Subject("first name", "John").equals("Student"));
		Assert.assertTrue(new Subject().equals(new Subject()));
		
		// check only name
		Assert.assertTrue(new Subject("first name").equals(new Subject("first name")));
		Assert.assertFalse(new Subject("first name").equals(new Subject("last name")));
		Assert.assertFalse(new Subject("first name").equals(new Subject()));
		Assert.assertFalse(new Subject().equals(new Subject("last name")));
		
		
		// check name and value
		Assert.assertTrue(new Subject("first name", "John").equals(new Subject("first name", "John")));
		Assert.assertFalse(new Subject("first name", "John").equals(new Subject("first name", "Johnny")));
		Assert.assertFalse(new Subject("last name", "John").equals(new Subject("first name", "John")));
		Assert.assertFalse(new Subject("first name", "John").equals(new Subject("first name")));
		Assert.assertFalse(new Subject("first name").equals(new Subject("first name", "John")));
	}
}
