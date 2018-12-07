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
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import personal.wuyi.jibernate.config.MysqlDbConfig;
import personal.wuyi.jibernate.entity.Ethnicity;
import personal.wuyi.jibernate.entity.Student;
import personal.wuyi.jibernate.entity.Uri;
import personal.wuyi.jibernate.entitymanager.MysqlEntityManagerDao;
import personal.wuyi.jibernate.exception.DatabaseOperationException;
import personal.wuyi.jibernate.expression.Expression;
import personal.wuyi.jibernate.query.EntityQuery;

/**
 * Test class for AbstractEntityManagerDao
 * 
 * @author  Wuyi Chen
 * @date    08/30/2018
 * @version 1.0
 * @since   1.0
 */
public class AbstractEntityManagerDaoTest {
	private static MysqlDbConfig          dbConfig;
	private static MysqlEntityManagerDao  dao;
	
	@Before
	public void buildConnnection() throws IllegalArgumentException, IllegalAccessException, IOException {
		dbConfig   = new MysqlDbConfig("config/MysqlDb.properties").initialize();
		dao        = new MysqlEntityManagerDao(dbConfig);
		PropertyConfigurator.configure("config/Log4j.properties");
	}
	
	@Test
	public void writeTest() throws ParseException, DatabaseOperationException  {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		
	    Student student = new Student();
	    student.setFirstName("John");
	    student.setLastName("Doe");
	    student.setDob(df.parse("07/16/1993"));
	    student.setGpa(3.22);
	    student.setRace(Ethnicity.ASIAN);
		
		dao.write(student);
		
		Assert.assertTrue(student.isPersisted());
	}
	
	@Test
	public void readTest() {
		// basic query
		EntityQuery<Student> q1 = new EntityQuery<Student>(Student.class);
		q1.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
		List<Student> studentList = dao.read(q1);
		Student student = studentList.get(0);
		student.setFirstName("Alex");
		Uri uri = student.getUri();
		Assert.assertEquals("/personal/wuyi/jibernate/entity/Student/1", uri.toString());
		Assert.assertTrue(student.isPersisted());
		
		// query for only few columns
		EntityQuery<Student> q2 = new EntityQuery<Student>(Student.class);
		q2.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
		List<List<?>> listList = dao.read(q1, "firstName", "lastName");
		for (List<?> list : listList) {
			Assert.assertEquals("John", (String) list.get(0));
			Assert.assertEquals("Doe", (String) list.get(1));
		}
	}
	
	@Test
	public void countTest() {
		// basic query
		EntityQuery<Student> q1 = new EntityQuery<Student>(Student.class);
		q1.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
		long count = dao.count(q1);
		System.out.println(count);
	}
}
