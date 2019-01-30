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

import junit.framework.Assert;

import org.junit.Test;

import personal.wuyi.jibernate.entity.Student;

/**
 * Test class for {@code Subject}.
 * 
 * @author  Wuyi Chen
 * @date    01/28/2018
 * @version 1.1
 * @since   1.1
 */
public class EntityQueryTest {
	@Test
	public void setJpqlTest() {
		EntityQuery<Student> eq = new EntityQuery<>(Student.class);
		eq.setJpql("test");
		Assert.assertEquals("test", eq.getJpql());
	}
}
