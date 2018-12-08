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
	private MysqlDbConfig          dbConfig;
	private MysqlEntityManagerDao  dao;
	
	private GenericDbConfig        config;
	private GenericDbClient        dbService;
	
	@Before
	public void buildConnnection() throws IllegalArgumentException, IllegalAccessException, IOException, SQLException, ClassNotFoundException {
		// build dao connection
		dbConfig   = new MysqlDbConfig("config/MysqlDb.properties").initialize();
		dao        = new MysqlEntityManagerDao(dbConfig);
		
		// build generic database connection for testing purpose only
		config    = new GenericDbConfig(dbConfig.getHost(), dbConfig.getPort(), dbConfig.getDatabase(), dbConfig.getUsername(), dbConfig.getPassword());
		dbService = new GenericDbClient(DbType.MYSQL);
		dbService.buildConnection(config);
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
	
	@Test
	public void readTest() throws SQLException {
		// basic query
		EntityQuery<Student> q1 = new EntityQuery<Student>(Student.class);
		q1.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
	    q1.setSort(new Sort("id", true));
		List<Student> studentList = dao.read(q1);
				
		String sql = "select * from student where first_name = 'John' order by id asc";
		ResultSet rs = dbService.executeQuery(sql);
		
		for (int i = 0; i < studentList.size(); i++) {
			Student student = studentList.get(i);
			rs.next();
			
			Assert.assertEquals((long) rs.getLong("id"),    (long) student.getId());
			Assert.assertEquals(rs.getString("first_name"), student.getFirstName());
			Assert.assertEquals(rs.getString("last_name"),  student.getLastName());
			Assert.assertEquals(rs.getDate("dob"),          student.getDob());
			Assert.assertEquals(rs.getDouble("gpa"),        student.getGpa(), 0.0);
			Assert.assertEquals(rs.getString("race"),       student.getRace().toString());
		}
		
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
