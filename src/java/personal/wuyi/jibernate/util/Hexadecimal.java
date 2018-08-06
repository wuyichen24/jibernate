package personal.wuyi.autostock.util;

import java.math.BigInteger;


/**
 * Hexadecimal
 */
public class Hexadecimal {
	public static String encode(byte[] content) {
		return new BigInteger(1, content).toString(16);
	}
}
