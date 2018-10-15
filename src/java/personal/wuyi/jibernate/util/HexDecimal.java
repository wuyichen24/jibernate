/*
 * Copyright 2018 Wuyi Chen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
