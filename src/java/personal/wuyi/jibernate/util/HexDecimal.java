package personal.wuyi.jibernate.util;

import java.math.BigInteger;

/**
 * The class for generating hex string of a byte array.
 * 
 * @author  Wuyi Chen
 * @date    10/10/2018
 * @version 1.0
 * @since   1.0
 */
public class HexDecimal {
	private HexDecimal() {}
	
	/**
	 * Encode a byte array into a hex string
	 * 
	 * @param  array
	 *         The byte array needs to be encoded.
	 *         
	 * @return  The hex string of the byte array.
	 * 
     * @since   1.0
	 */
	public static String encode(byte[] array) {
		return new BigInteger(1, array).toString(16);
	}
}
