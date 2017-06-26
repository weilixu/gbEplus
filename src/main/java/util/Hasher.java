package main.java.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {
    /**
     * method supports:<br/>
     * <ul>
     *     <li>MD5: 32 letters</li>
     *     <li>SHA1: 40 letters</li>
     *     <li>SHA256: 64 letters</li>
     * </ul>
     * @param str
     * @param method
     * @return
     */
    public static String hash(String str){
        MessageDigest mdAlgorithm = null;
		try {
			mdAlgorithm = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};

        mdAlgorithm.update(str.getBytes());
        byte[] digest = mdAlgorithm.digest();
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            str = Integer.toHexString(0xFF & digest[i]);
            if (str.length() < 2) {
                str = "0" + str;
            }
            hexString.append(str);
        }
        return hexString.toString();
    }
}
