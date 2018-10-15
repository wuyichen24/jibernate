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

package personal.wuyi.jibernate.entity;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for Uri.
 * 
 * @author  Wuyi Chen
 * @date    08/30/2018
 * @version 1.0
 * @since   1.0
 */
public class UriTest {
	@Test
	public void constructorTest() {
		Uri uri1 = new Uri(Student.class);
		Assert.assertEquals("/personal/wuyi/jibernate/entity/Student/", uri1.toString());
		Assert.assertEquals(Student.class,                              uri1.getType());
		
		Uri uri2 = new Uri(Student.class, 24);
		Assert.assertEquals("/personal/wuyi/jibernate/entity/Student/24", uri2.toString());
		Assert.assertEquals(Student.class,                                uri2.getType());
		Assert.assertEquals(24,                                           uri2.getId());
	}
	
	@Test
	public void getTypeTest() throws ClassNotFoundException {
		Assert.assertEquals(Student.class, Uri.getType("/personal/wuyi/jibernate/entity/Student/"));
		Assert.assertEquals(Student.class, Uri.getType("personal/wuyi/jibernate/entity/Student/"));
		Assert.assertEquals(Student.class, Uri.getType("/personal/wuyi/jibernate/entity/Student"));
		Assert.assertEquals(Student.class, Uri.getType("personal/wuyi/jibernate/entity/Student"));
		Assert.assertEquals(null, Uri.getType(null));
		Assert.assertEquals(null, Uri.getType(""));
		Assert.assertEquals(null, Uri.getType("/personal/wuyi/jibernate/entity/XYZ/"));
	}
	
	@Test
	public void parseTest() {
		Uri uri = Uri.parse("/personal/wuyi/jibernate/entity/Student/27");
		Assert.assertEquals(Student.class,                                uri.getType());
		Assert.assertEquals(27,                                           uri.getId());
	}
	
	@Test
	public void getPathTest() {
		Assert.assertEquals("/personal/wuyi/jibernate/entity/Student/", Uri.getPath(Student.class));
	}
	
	@Test
	public void equalsTest() {
		Assert.assertTrue((new Uri(Student.class, 24)).equals(new Uri(Student.class, 24)));
		Assert.assertFalse((new Uri(Student.class, 24)).equals(new Uri(Student.class, 27)));
	}
}
