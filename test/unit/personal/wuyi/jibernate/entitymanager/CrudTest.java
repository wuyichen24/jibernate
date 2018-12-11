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

package personal.wuyi.jibernate.entitymanager;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import personal.wuyi.jibernate.config.MysqlDbConfig;
import personal.wuyi.jibernate.entity.Ethnicity;
import personal.wuyi.jibernate.entity.Student;
import personal.wuyi.jibernate.entitymanager.MysqlEntityManagerDao;
import personal.wuyi.jibernate.exception.DatabaseOperationException;
import personal.wuyi.jibernate.expression.Expression;
import personal.wuyi.jibernate.query.EntityQuery;

/**
 * Test class for doing basic database operation.
 * 
 * @author  Wuyi Chen
 * @date    08/30/2018
 * @version 1.0
 * @since   1.0
 */
public class CrudTest {
	private static MysqlDbConfig          dbConfig;
	private static MysqlEntityManagerDao  dao;
	
	@Before
	public void buildConnnection() throws IllegalArgumentException, IllegalAccessException, IOException {
		dbConfig   = new MysqlDbConfig("config/MysqlDb.properties").initialize();
		dao        = new MysqlEntityManagerDao(dbConfig);
		PropertyConfigurator.configure("config/Log4j.properties");
	}
	
	@Test
	public void insertTest() throws ParseException, DatabaseOperationException  {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		
	    Student student = new Student();
	    student.setFirstName("Mary");
	    student.setLastName("Wang");
	    student.setDob(df.parse("07/16/1994"));
	    student.setGpa(3.43);
	    student.setRace(Ethnicity.WHITE);
		
		dao.write(student);
	}
	
	@Test
	public void queryTest() {
		// basic query
		EntityQuery<Student> q1 = new EntityQuery<Student>(Student.class);
		q1.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
		List<Student> studentList = dao.read(q1);
		System.out.println(studentList.size());
		
		// query for only few columns
		EntityQuery<Student> q2 = new EntityQuery<>(Student.class);
		q2.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
		List<List<?>> listList = dao.read(q1, "firstName", "lastName");
		for (List<?> list : listList) {
			for (Object obj : list) {
				String str = (String) obj;
				System.out.println(str);
			}
		}
		
		// Test IN expression
		System.out.println();
		EntityQuery<Student> q3 = new EntityQuery<Student>(Student.class);
		q3.setCriteria(new Expression("firstName", Expression.IN, Arrays.asList("John", "Mary")));
		List<Student> studentList3 = dao.read(q3);
		for (Student student : studentList3) {
			System.out.println(student.getFirstName());
		}
	}
	
	@Test
	public void updateTest() throws DatabaseOperationException {
		EntityQuery<Student> q1 = new EntityQuery<Student>(Student.class);
		q1.setCriteria(new Expression("firstName", Expression.EQUAL, "Mary"));
		List<Student> studentList = dao.read(q1);
		for (Student student : studentList) {
			student.setGpa(student.getGpa() * 1.1);
		}
		dao.write(studentList);
	}
	
	@Test
	public void deleteTest() throws DatabaseOperationException {
		EntityQuery<Student> q1 = new EntityQuery<Student>(Student.class);
		q1.setCriteria(new Expression("firstName", Expression.EQUAL, "Mary"));
		List<Student> studentList = dao.read(q1);
		dao.delete(studentList.get(0));
	}
}
