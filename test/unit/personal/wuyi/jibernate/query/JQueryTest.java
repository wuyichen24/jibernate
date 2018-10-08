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
 * @author  Wuyi Chen
 * @date    09/19/2018
 * @version 1.0
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
		// test ascending
		EntityQuery<Student> q1 = new EntityQuery<Student>(Student.class);
		q1.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
		q1.setSort("gpa+");
		List<Student> studentList1 = dao.read(q1);
		List<Double> gpaList1 = new ArrayList<>();
		for (Student student : studentList1) {
			gpaList1.add(student.getGpa());
		}
		Assert.assertTrue(Ordering.natural().isOrdered(gpaList1));
		
		// test descending
		EntityQuery<Student> q2 = new EntityQuery<Student>(Student.class);
		q2.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
		q2.setSort("gpa-");
		List<Student> studentList2 = dao.read(q2);
		List<Double> gpaList2 = new ArrayList<>();
		for (Student student : studentList2) {
			gpaList2.add(student.getGpa());
		}
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(gpaList2));
		
	}
}
