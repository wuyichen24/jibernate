package personal.wuyi.jibernate.entity;

import org.junit.Assert;
import org.junit.Test;

public class UriTest {
	@Test
	public void constructorTest() {
		Uri uri1 = new Uri(Student.class);
		Assert.assertEquals("/personal/wuyi/jibernate/entity/Student/", uri1.toString());
		Assert.assertEquals(Student.class,                              uri1.getType());
		
		Uri uri2 = new Uri(Student.class, 24);
		Assert.assertEquals("/personal/wuyi/jibernate/entity/Student/24", uri2.toString());
		Assert.assertEquals(Student.class,                                uri2.getType());
		Assert.assertEquals(24,                                           uri2.getId());
	}
	
	@Test
	public void getTypeTest() throws ClassNotFoundException {
		Assert.assertEquals(Student.class, Uri.getType("/personal/wuyi/jibernate/entity/Student/"));
		Assert.assertEquals(null, Uri.getType(null));
		Assert.assertEquals(null, Uri.getType(""));
		Assert.assertEquals(null, Uri.getType("/personal/wuyi/jibernate/entity/XYZ/"));
	}
}
