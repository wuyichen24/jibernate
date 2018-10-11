package personal.wuyi.jibernate.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5
 * 
 * @author  Wuyi Chen
 * @date    10/10/2018
 * @version 1.0
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
