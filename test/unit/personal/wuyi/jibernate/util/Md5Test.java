package personal.wuyi.jibernate.util;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.junit.Assert;
import org.junit.Test;

/**
 * The test class for {@code Md5}.
 * 
 * @author  Wuyi Chen
 * @date    10/10/2018
 * @version 1.1
 * @since   1.0
 */
public class Md5Test {
	@Test
	public void hashTest() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		Assert.assertEquals("86fb269d190d2c85f6e0468ceca42a20", Md5.hash("Hello world!"));
		Assert.assertEquals("d41d8cd98f00b204e9800998ecf8427e", Md5.hash(""));
	}
}
