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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import personal.wuyi.client.database.DbType;
import personal.wuyi.client.database.GenericDbClient;
import personal.wuyi.client.database.GenericDbConfig;
import personal.wuyi.jibernate.config.MysqlDbConfig;
import personal.wuyi.jibernate.entity.Ethnicity;
import personal.wuyi.jibernate.entity.Student;
import personal.wuyi.jibernate.entity.Uri;
import personal.wuyi.jibernate.entitymanager.MysqlEntityManagerDao;
import personal.wuyi.jibernate.exception.DatabaseOperationException;
import personal.wuyi.jibernate.expression.Expression;
import personal.wuyi.jibernate.query.EntityQuery;
import personal.wuyi.jibernate.query.Sort;
import personal.wuyi.jibernate.test.GenericDbClientUtil;

/**
 * Test class for AbstractEntityManagerDao
 * 
 * @author  Wuyi Chen
 * @date    08/30/2018
 * @version 1.0
 * @since   1.0
 */
public class AbstractEntityManagerDaoTest {
	private MysqlEntityManagerDao  dao;
	
	private GenericDbClient        dbService;
	
	@Before
	public void buildConnnection() throws IllegalArgumentException, IllegalAccessException, IOException, SQLException, ClassNotFoundException {
		// build dao connection
		MysqlDbConfig dbConfig = new MysqlDbConfig("config/MysqlDb.properties").initialize();
		dao                    = new MysqlEntityManagerDao(dbConfig);
		
		// build generic database connection for testing purpose only
		dbService = new GenericDbClient(DbType.MYSQL);
		dbService.buildConnection(new GenericDbConfig(dbConfig.getHost(), dbConfig.getPort(), dbConfig.getDatabase(), dbConfig.getUsername(), dbConfig.getPassword()));
		PropertyConfigurator.configure("config/Log4j.properties");
	}
	
	@Test
	public void writeTest() throws ParseException, DatabaseOperationException, SQLException  {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		
		int orignalCount = GenericDbClientUtil.getNumberOfRecords(dbService, "student", "first_name = 'John'");
		
	    Student student = new Student();
	    student.setFirstName("John");
	    student.setLastName("Doe");
	    student.setDob(df.parse("07/16/1993"));
	    student.setGpa(3.22);
	    student.setRace(Ethnicity.ASIAN);
		dao.write(student);
		
		int newCount = GenericDbClientUtil.getNumberOfRecords(dbService, "student", "first_name = 'John'");
		
		Assert.assertEquals(orignalCount + 1, newCount);
		Assert.assertTrue(student.isPersisted());
	}
	
	/**
	 * Test reading a list of objects by inputing an {@code EntityQuery} object.
	 */
	@Test
	public void readTest1() throws SQLException {
		EntityQuery<Student> q1 = new EntityQuery<Student>(Student.class);
		q1.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
	    q1.setSort(new Sort("id", true));
		List<Student> studentList = dao.read(q1);
		
		// use general db client to read data for assertions
		String sql = "select * from student where first_name = 'John' order by id asc";
		ResultSet rs = dbService.executeQuery(sql);
		
		for (int i = 0; i < studentList.size(); i++) {
			Student student = studentList.get(i);
			if (rs.next()){
				Assert.assertEquals((long) rs.getLong("id"),    (long) student.getId());
				Assert.assertEquals(rs.getString("first_name"), student.getFirstName());
				Assert.assertEquals(rs.getString("last_name"),  student.getLastName());
				Assert.assertEquals(rs.getDate("dob"),          student.getDob());
				Assert.assertEquals(rs.getDouble("gpa"),        student.getGpa(), 0.0);
				Assert.assertEquals(rs.getString("race"),       student.getRace().toString());
			}
			Assert.assertTrue(student.isPersisted());
			Assert.assertEquals("/personal/wuyi/jibernate/entity/Student/"+ rs.getLong("id"), student.getUri().toString());
		}
		
		// test result set is empty
		EntityQuery<Student> q2 = new EntityQuery<Student>(Student.class);
		q2.setCriteria(new Expression("firstName", Expression.EQUAL, "Manson"));
		List<Student> studentList2 = dao.read(q2);
		Assert.assertTrue(studentList2.isEmpty());
	}
	
	/**
	 * Test reading a list of objects only with few columns.
	 */
	@Test
	public void readTest2() {
		// test read 2 or more fields
		EntityQuery<Student> q1 = new EntityQuery<Student>(Student.class);
		q1.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
		List<List<?>> listList1 = dao.read(q1, "firstName", "lastName");
		for (List<?> list : listList1) {
			Assert.assertEquals("John", (String) list.get(0));
			Assert.assertEquals("Doe", (String) list.get(1));
		}
		
		// test read only 1 field
		EntityQuery<Student> q2 = new EntityQuery<Student>(Student.class);
		q2.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
		List<List<?>> listList2 = dao.read(q2, "firstName");
		for (List<?> list : listList2) {
			Assert.assertEquals("John", (String) list.get(0));
		}
	}
	
	/**
	 * Test reading only one object by inputing a {@code Uri} object.
	 */
	@Test
	public void readTest3() throws SQLException {
		String sql = "select * from student where first_name = 'John' order by id asc";
		ResultSet rs = dbService.executeQuery(sql);
		
		while (rs.next()) {
			Uri uri = new Uri(Student.class, rs.getLong("id"));
			Student student = dao.read(uri);
			Assert.assertEquals((long) rs.getLong("id"),    (long) student.getId());
			Assert.assertEquals(rs.getString("first_name"), student.getFirstName());
			Assert.assertEquals(rs.getString("last_name"),  student.getLastName());
			Assert.assertEquals(rs.getDate("dob"),          student.getDob());
			Assert.assertEquals(rs.getDouble("gpa"),        student.getGpa(), 0.0);
			Assert.assertEquals(rs.getString("race"),       student.getRace().toString());
		}
	}
	
	@Test
	public void readExceptionTest() {
		try {
			Uri uri = new Uri(Student.class);
			dao.read(uri);
			fail("Expected an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("id can not be null when you query by primary key."));
		}
	}
	
	@Test
	public void countTest() throws SQLException {
		int expectedCount = GenericDbClientUtil.getNumberOfRecords(dbService, "student", "first_name = 'John'");
		
		EntityQuery<Student> q1 = new EntityQuery<Student>(Student.class);
		q1.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
		long count = dao.count(q1);
	
		Assert.assertEquals(expectedCount, count);
	}
	
	@After
	public void closeConnection() throws SQLException {
		dbService.closeConnection();
	}
}
