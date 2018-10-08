package personal.wuyi.jibernate.query;

import org.junit.Test;

import junit.framework.Assert;
import personal.wuyi.jibernate.entity.Student;
import personal.wuyi.jibernate.expression.Expression;

public class QueryConverterTest {
	@Test
	public void transformTest() {
		
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
	
	public void buildWhereClauseTest() {
		
	}
}
