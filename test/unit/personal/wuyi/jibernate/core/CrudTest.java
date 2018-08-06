package personal.wuyi.jibernate.core;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;

import personal.wuyi.jibernate.config.MysqlDbConfig;
import personal.wuyi.jibernate.entity.Ethnicity;
import personal.wuyi.jibernate.entity.Student;

public class CrudTest {
	private static MysqlDbConfig          dbConfig;
	private static MysqlEntityManagerDao  dao;
	
	@Before
	public void buildConnnection() throws IllegalArgumentException, IllegalAccessException, IOException {
		dbConfig   = new MysqlDbConfig("config/MysqlDb.properties").initialize();
		dao        = new MysqlEntityManagerDao(dbConfig);
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
}
