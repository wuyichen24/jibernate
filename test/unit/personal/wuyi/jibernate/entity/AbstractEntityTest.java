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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import personal.wuyi.jibernate.config.MysqlDbConfig;
import personal.wuyi.jibernate.entitymanager.MysqlEntityManagerDao;
import personal.wuyi.jibernate.expression.Expression;
import personal.wuyi.jibernate.query.EntityQuery;
import personal.wuyi.jibernate.query.Sort;

/**
 * Test class for {@code AbstractEntity}.
 * 
 * <p>The {@code AbstractEntity} class is an abstract class, use its concrete 
 * class to test it.
 * 
 * @author  Wuyi Chen
 * @date    12/10/2018
 * @version 1.1
 * @since   1.1
 */
public class AbstractEntityTest {
	private MysqlEntityManagerDao  dao;
	
	@Before
	public void buildConnnection() throws IllegalArgumentException, IllegalAccessException, IOException, SQLException, ClassNotFoundException {
		// build dao connection
		MysqlDbConfig dbConfig = new MysqlDbConfig("config/MysqlDb.properties").initialize();
		dao                    = new MysqlEntityManagerDao(dbConfig);
	}
	
	@Test
	public void getUriTest() {
		EntityQuery<Student> q1 = new EntityQuery<Student>(Student.class);
		q1.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
	    q1.setSort(new Sort("id", true));
		List<Student> studentList = dao.read(q1);
		
		for (Student student : studentList) {
			Assert.assertEquals("/personal/wuyi/jibernate/entity/Student/"+ student.getId(), student.getUri().toString());
		}
	}
	
	@Test
	public void isPersistedTest() {
		EntityQuery<Student> q1 = new EntityQuery<Student>(Student.class);
		q1.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
	    q1.setSort(new Sort("id", true));
		List<Student> studentList = dao.read(q1);
		
		// records from database should be persisted.
		for (Student student : studentList) {
			Assert.assertTrue(student.isPersisted());
		}
	}
}
