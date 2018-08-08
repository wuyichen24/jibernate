package personal.wuyi.jibernate.core;

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
import personal.wuyi.jibernate.expression.Expression;
import personal.wuyi.jibernate.query.EntityQuery;

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
	public void insertTest() throws ParseException  {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		
	    Student student = new Student();
	    student.setFirstName("John");
	    student.setLastName("Doe");
	    student.setDob(df.parse("07/16/1993"));
	    student.setGpa(3.45);
	    student.setRace(Ethnicity.ASIAN);
		
		dao.write(student);
	}
	
	@Test
	public void queryTest() {
		// basic query
		EntityQuery<Student> q1 = new EntityQuery<Student>(Student.class);
		q1.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
		List<Student> studentList = dao.read(q1);
		Assert.assertEquals(2, studentList.size());
		
		// query for only few columns
		EntityQuery<Student> q2 = new EntityQuery<Student>(Student.class);
		q2.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
		List<List<?>> listList = dao.read(q1, "firstName", "lastName");
		for (List<?> list : listList) {
			for (Object obj : list) {
				String str = (String) obj;
				System.out.println(str);
			}
		}
	}
}
