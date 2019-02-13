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

import org.junit.Assert;
import org.junit.Test;

/**
 * The test class for {@code Sort}.
 * 
 * @author  Wuyi Chen
 * @date    10/09/2018
 * @version 1.1
 * @since   1.0
 */
public class SortTest {
	@Test
	public void setFieldTest() {
		Sort sort = new Sort("firstname");
		sort.setField("lastname");
		Assert.assertEquals("lastname", sort.getField());
	}
	
	@Test
	public void setAscendingTest() {
		Sort sort = new Sort("firstname", true);
		sort.setAscending(false);
		Assert.assertFalse(sort.isAscending());
	}
	
	@Test
	public void parseTest() {
		Sort sort1 = Sort.parse("firstname+");
		Assert.assertEquals("firstname", sort1.getField());
		Assert.assertTrue(sort1.isAscending());
		
		Sort sort2 = Sort.parse("firstname-");
		Assert.assertEquals("firstname", sort2.getField());
		Assert.assertFalse(sort2.isAscending());
		
		Sort sort3 = Sort.parse("firstname+,lastname-,gpa-");
		Assert.assertEquals("firstname", sort3.toList().get(0).getField());
		Assert.assertEquals("lastname",  sort3.toList().get(1).getField());
		Assert.assertEquals("gpa",       sort3.toList().get(2).getField());
		Assert.assertTrue(sort3.toList().get(0).isAscending());
		Assert.assertFalse(sort3.toList().get(1).isAscending());
		Assert.assertFalse(sort3.toList().get(2).isAscending());
	}
	
	@Test
	public void addTest() {
		// test {firstname+, lastname-}
		Sort sort1 = new Sort("firstname", true);
		sort1.add("lastname", false);
		Assert.assertEquals("firstname", sort1.toList().get(0).getField());
		Assert.assertEquals("lastname",  sort1.toList().get(1).getField());
		Assert.assertTrue(sort1.toList().get(0).isAscending());
		Assert.assertFalse(sort1.toList().get(1).isAscending());
		
		// test {firstname+, lastname-, gpa+, age-}
		Sort sort2 = new Sort("gpa", true);
		sort2.add("age", false);
		sort1.add(sort2);
		Assert.assertEquals("firstname", sort1.toList().get(0).getField());
		Assert.assertEquals("lastname",  sort1.toList().get(1).getField());
		Assert.assertEquals("gpa",       sort1.toList().get(2).getField());
		Assert.assertEquals("age",       sort1.toList().get(3).getField());
		Assert.assertTrue(sort1.toList().get(0).isAscending());
		Assert.assertFalse(sort1.toList().get(1).isAscending());
		Assert.assertTrue(sort1.toList().get(2).isAscending());
		Assert.assertFalse(sort1.toList().get(3).isAscending());
		
		// test {firstname+, lastname-, gpa+, age-, race+}
		sort1.add("race");                                                   // add field name (use default ascending order)
		Assert.assertEquals("firstname", sort1.toList().get(0).getField());
		Assert.assertEquals("lastname",  sort1.toList().get(1).getField());
		Assert.assertEquals("gpa",       sort1.toList().get(2).getField());
		Assert.assertEquals("age",       sort1.toList().get(3).getField());
		Assert.assertEquals("race",      sort1.toList().get(4).getField());
		Assert.assertTrue(sort1.toList().get(0).isAscending());
		Assert.assertFalse(sort1.toList().get(1).isAscending());
		Assert.assertTrue(sort1.toList().get(2).isAscending());
		Assert.assertFalse(sort1.toList().get(3).isAscending());
		Assert.assertTrue(sort1.toList().get(4).isAscending());
	}
	
	@Test
	public void toStringTest() {
		Sort sort1 = new Sort("firstname", true);
		Assert.assertEquals("firstname+", sort1.toString());
		
		// test multiple sorts
		Sort sort2 = new Sort("firstname", true).add("lastname", false).add("gpa", true).add("age", false);
		Assert.assertEquals("firstname+,lastname-,gpa+,age-", sort2.toString());
	}
	
	@Test
	public void equalsTest() {
		// test some special cases
		Assert.assertFalse(Sort.parse("firstname+").equals(null));
		Assert.assertFalse(Sort.parse("firstname+").equals("ABC"));
		
		// test single column sort
		Assert.assertTrue(Sort.parse("firstname+").equals(Sort.parse("firstname+")));
		Assert.assertFalse(Sort.parse("firstname+").equals(Sort.parse("firstname-")));
		Assert.assertFalse(Sort.parse("firstname+").equals(Sort.parse("lastname+")));
		
		// test multiple column sort
		Assert.assertTrue(Sort.parse("firstname+,lastname-").equals(Sort.parse("firstname+,lastname-")));
		Assert.assertFalse(Sort.parse("firstname+,lastname-").equals(Sort.parse("firstname+,lastname+")));
		Assert.assertFalse(Sort.parse("firstname+,lastname-").equals(Sort.parse("lastname-,firstname+")));  // order is priority, the order should be same
		Assert.assertFalse(Sort.parse("firstname+,lastname-").equals(Sort.parse("firstname+")));
		
		// test multiple sort and single sort comparison
		Sort mulSort1 = new Sort("firstname").add("lastname");
		Sort mulSort2 = new Sort("firstname").add("lastname").add("age");
		Sort sinSort = new Sort("age");
		Assert.assertFalse(mulSort1.equals(sinSort));
		Assert.assertFalse(sinSort.equals(mulSort1));
		Assert.assertFalse(mulSort1.equals(mulSort2));
		Assert.assertFalse(mulSort2.equals(mulSort1));
	}
}
