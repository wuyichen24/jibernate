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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5
 * 
 * @author  Wuyi Chen
 * @date    10/10/2018
 * @version 1.1
 * @since   1.0
 */
public abstract class Md5 {
    private Md5() {}

    /**
     * Generate MD5 hash and return as Hex string.
     *
     * @param  text
     *         The text needs to be generated MD5.
     * 
     * @return  The Hex string of the MD5 hash of the input text.
     *  
     * @throws  UnsupportedEncodingException
     *          If the charset of the input text is not supported.
     *          
     * @throws  NoSuchAlgorithmException
     *          If the algorithm (MD5) is not available.
     *          
     * @since   1.0
     */
    public static String hash(String text) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] textBytes = text.getBytes("UTF-8");

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(textBytes);

        return HexDecimal.encode(digest);
    }
}
