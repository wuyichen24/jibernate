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
 * @version 1.0
 * @since   1.0
 */
public class SubjectTest {
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
		Subject sub1 = new Subject("first name", "John");
		
		Assert.assertFalse(sub1.equals(null));
		Assert.assertFalse("first name".equals(sub1));
		
		Subject sub2 = new Subject("first name", "Mary");
		Assert.assertFalse(sub1.equals(sub2));
		
		Subject sub3 = new Subject("last name", "John");
		Assert.assertFalse(sub1.equals(sub3));
		
		Subject sub4 = new Subject("first name", "John");
		Assert.assertTrue(sub1.equals(sub4));
	}
}
