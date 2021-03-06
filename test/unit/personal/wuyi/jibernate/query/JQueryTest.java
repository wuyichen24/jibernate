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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Ordering;

import personal.wuyi.jibernate.config.MysqlDbConfig;
import personal.wuyi.jibernate.entity.Student;
import personal.wuyi.jibernate.entitymanager.MysqlEntityManagerDao;
import personal.wuyi.jibernate.expression.Expression;

/**
 * The test class for {@code Query}.
 * 
 * <p>This class is with DB connection.
 * 
 * @author  Wuyi Chen
 * @date    09/19/2018
 * @version 1.1
 * @since   1.0
 */
public class JQueryTest {
	private static MysqlDbConfig          dbConfig;
	private static MysqlEntityManagerDao  dao;
	
	@Before
	public void buildConnnection() throws IllegalArgumentException, IllegalAccessException, IOException {
		dbConfig   = new MysqlDbConfig("config/MysqlDb.properties").initialize();
		dao        = new MysqlEntityManagerDao(dbConfig);
		PropertyConfigurator.configure("config/Log4j.properties");
	}
	
	@Test
	public void setSortTest() {
		// test ascending (single column)
		EntityQuery<Student> q1 = new EntityQuery<Student>(Student.class);
		q1.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
		q1.setSort("gpa+");
		List<Student> studentList1 = dao.read(q1);
		List<Double> gpaList1 = new ArrayList<>();
		for (Student student : studentList1) {
			gpaList1.add(student.getGpa());
		}
		Assert.assertTrue(Ordering.natural().isOrdered(gpaList1));
		
		// test descending (single column)
		EntityQuery<Student> q2 = new EntityQuery<Student>(Student.class);
		q2.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
		q2.setSort("gpa-");
		List<Student> studentList2 = dao.read(q2);
		List<Double> gpaList2 = new ArrayList<>();
		for (Student student : studentList2) {
			gpaList2.add(student.getGpa());
		}
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(gpaList2));
		
		// test ascending (multiple columns)
		EntityQuery<Student> q3 = new EntityQuery<Student>(Student.class);
		q3.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
		q3.setSort("gpa+", "dob-");
		List<Student> studentList3 = dao.read(q3);
		List<Double> gpaList3 = new ArrayList<>();
		for (Student student : studentList3) {
			gpaList3.add(student.getGpa());
		}
		Assert.assertTrue(Ordering.natural().isOrdered(gpaList3));
	}
	
	@Test
	public void setLimitTest() {
		EntityQuery<Student> q1 = new EntityQuery<Student>(Student.class);
		q1.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
		q1.setLimit(4);
		List<Student> studentList = dao.read(q1);
		Assert.assertEquals(4, studentList.size());
	}
	
	@Test
	public void setOffsetTest() {
		EntityQuery<Student> q1 = new EntityQuery<Student>(Student.class);
		q1.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
		q1.setOffset(2);
		List<Student> studentList = dao.read(q1);

		for (Student stu : studentList) {
			System.out.println(stu.getId());
		}
	}
}
