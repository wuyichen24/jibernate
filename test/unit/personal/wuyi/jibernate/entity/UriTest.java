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
		Assert.assertEquals(Student.class, Uri.getType("personal/wuyi/jibernate/entity/Student/"));
		Assert.assertEquals(Student.class, Uri.getType("/personal/wuyi/jibernate/entity/Student"));
		Assert.assertEquals(Student.class, Uri.getType("personal/wuyi/jibernate/entity/Student"));
		Assert.assertEquals(null, Uri.getType(null));
		Assert.assertEquals(null, Uri.getType(""));
		Assert.assertEquals(null, Uri.getType("/personal/wuyi/jibernate/entity/XYZ/"));
	}
	
	@Test
	public void parseTest() {
		Uri uri = Uri.parse("/personal/wuyi/jibernate/entity/Student/27");
		Assert.assertEquals(Student.class,                                uri.getType());
		Assert.assertEquals(27,                                           uri.getId());
	}
	
	@Test
	public void getPathTest() {
		Assert.assertEquals("/personal/wuyi/jibernate/entity/Student/", Uri.getPath(Student.class));
	}
	
	@Test
	public void equalsTest() {
		Assert.assertTrue((new Uri(Student.class, 24)).equals(new Uri(Student.class, 24)));
		Assert.assertFalse((new Uri(Student.class, 24)).equals(new Uri(Student.class, 27)));
	}
}
