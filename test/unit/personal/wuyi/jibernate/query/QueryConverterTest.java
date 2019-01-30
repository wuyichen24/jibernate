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

import java.util.Arrays;

import org.junit.Test;

import junit.framework.Assert;
import personal.wuyi.jibernate.entity.Student;
import personal.wuyi.jibernate.expression.Expression;

/**
 * The test class for {@code QueryConverter}.
 * 
 * @author  Wuyi Chen
 * @date    10/08/2018
 * @version 1.0
 * @since   1.0
 */
public class QueryConverterTest {
	@Test
	public void transformTest1() {
//		JQuery<Student> jq1 = new JQuery<Student>(Student.class);
//		jq1.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
//		Assert.assertEquals(jq1, QueryConverter.transform(jq1));
	}
	
	@Test
	public void transformTest2() {
		
	}
	
	@Test
	public void getJpqlStatementTest() {
		EntityQuery<Student> q1 = new EntityQuery<Student>(Student.class);
		q1.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
		String js1 = QueryConverter.getJpqlStatement(q1);
		Assert.assertEquals("SELECT student FROM Student student WHERE student.firstName = :STUDENT_FIRSTNAME_61409aa1fd47d4a5332de23cbf59a36f", js1);
	}
	
	@Test
	public void buildBasicSelectStatementTest() {
		Assert.assertEquals("SELECT student FROM Student student",                                           QueryConverter.buildBasicSelectStatement(Student.class, false));
		Assert.assertEquals("SELECT student.firstname,student.gpa FROM Student student",                     QueryConverter.buildBasicSelectStatement(Student.class, false, new String[] {"firstname", "gpa"}));
		Assert.assertEquals("SELECT DISTINCT(student.firstname),DISTINCT(student.gpa) FROM Student student", QueryConverter.buildBasicSelectStatement(Student.class, true,  new String[] {"firstname", "gpa"}));
		Assert.assertEquals("SELECT COUNT(student) FROM Student student",                                    QueryConverter.buildBasicSelectStatement(Student.class, false, new String[] {"COUNT(*)"}));
		Assert.assertEquals("SELECT COUNT(student) FROM Student student",                                    QueryConverter.buildBasicSelectStatement(Student.class, true,  new String[] {"COUNT(*)"}));
	}
	
	@Test
	public void buildSelectClauseTest() {
		Assert.assertEquals("student",                                           QueryConverter.buildSelectClause(Student.class, false));
		Assert.assertEquals("student.firstname,student.gpa",                     QueryConverter.buildSelectClause(Student.class, false, new String[] {"firstname", "gpa"}));
		Assert.assertEquals("DISTINCT(student.firstname),DISTINCT(student.gpa)", QueryConverter.buildSelectClause(Student.class, true,  new String[] {"firstname", "gpa"}));
		Assert.assertEquals("COUNT(student)",                                    QueryConverter.buildSelectClause(Student.class, false, new String[] {"COUNT(*)"}));
		Assert.assertEquals("COUNT(student)",                                    QueryConverter.buildSelectClause(Student.class, true,  new String[] {"COUNT(*)"}));
	}
	
	@Test
	public void buildWhereClauseTest() {
		Assert.assertEquals("WHERE UPPER(student.firstname) = :STUDENT_FIRSTNAME_61409aa1fd47d4a5332de23cbf59a36f", 
				QueryConverter.buildWhereClause(Student.class, new Expression("firstname", Expression.EQUAL, "John"), false));
		Assert.assertEquals("WHERE student.firstname = :STUDENT_FIRSTNAME_61409aa1fd47d4a5332de23cbf59a36f",        
				QueryConverter.buildWhereClause(Student.class, new Expression("firstname", Expression.EQUAL, "John"), true));
		Assert.assertEquals("WHERE student.firstname IS NULL",                                                      
				QueryConverter.buildWhereClause(Student.class, new Expression("firstname", Expression.EQUAL, null), true));                                       // test: is null
		Assert.assertEquals("WHERE student.firstname IS NOT NULL",                                                  
				QueryConverter.buildWhereClause(Student.class, new Expression("firstname", Expression.NOT_EQUAL, null), true));                                   // test: is not null
		Assert.assertEquals("WHERE student.firstname IN (:STUDENT_FIRSTNAME_9aefd7dd925d795da67a249e696d7a56)",     
				QueryConverter.buildWhereClause(Student.class, new Expression("firstname", Expression.IN, Arrays.asList("John", "Mary")), true));                 // test: in expression
		Assert.assertEquals("WHERE student.firstname = :STUDENT_FIRSTNAME_61409aa1fd47d4a5332de23cbf59a36f AND student.gpa = :STUDENT_GPA_2fe1d1290c6aeb51f235f9ffda8332db", 
				QueryConverter.buildWhereClause(Student.class, new Expression("firstname", Expression.EQUAL, "John").and("gpa", Expression.EQUAL, 3.45), true));  // test: compound expression
	}
	
	@Test
	public void buildOrderByClauseTest() {
		Assert.assertEquals("ORDER BY student.firstname",                   QueryConverter.buildOrderByClause(Student.class, new Sort("firstname", true)));
		Assert.assertEquals("ORDER BY student.firstname DESC",              QueryConverter.buildOrderByClause(Student.class, new Sort("firstname", false)));
		Assert.assertEquals("ORDER BY student.firstname, student.gpa DESC", QueryConverter.buildOrderByClause(Student.class, new Sort("firstname", true).add("gpa", false)));
	}
}
