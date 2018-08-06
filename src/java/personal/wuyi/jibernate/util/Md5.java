package personal.wuyi.autostock.util;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Md5
 */
public abstract class Md5 {

    private Md5() {

    }


    /**
     * Generate MD5 hash and return as Hex string.
     *
     * @param text
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    public static String hash( String text ) throws UnsupportedEncodingException, NoSuchAlgorithmException {


        byte[] textBytes = text.getBytes( "UTF-8" );

        MessageDigest md = MessageDigest.getInstance( "MD5" );
        byte[] digest = md.digest( textBytes );

        return Hexadecimal.encode( digest );
    }

}
