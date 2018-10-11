package personal.wuyi.jibernate.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * The test class for {@code HexDecimal}.
 * 
 * @author  Wuyi Chen
 * @date    10/10/2018
 * @version 1.0
 * @since   1.0
 */
public class HexDecimalTest {
	@Test
	public void encodeTest() {
		byte[] bytes1 = new byte[] {(byte) 0x00B2, (byte) 0x0062, (byte) 0x0047};
		Assert.assertEquals("b26247", HexDecimal.encode(bytes1));
		
		byte[] bytes2 = new byte[] {(byte) 0x00A3, (byte) 0x00A4, (byte) 0x00A5, (byte) 0x00A6, (byte) 0x00A7, (byte) 0x00A8};
		Assert.assertEquals("a3a4a5a6a7a8", HexDecimal.encode(bytes2));
	}
}
