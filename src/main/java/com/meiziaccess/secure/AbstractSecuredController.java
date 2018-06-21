/**
 * 
 */
package com.meiziaccess.secure;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author zhangle
 *
 */
public abstract class AbstractSecuredController  {

    public static String generateSign(HashMap<String, String> keyValues,
            String appKey, String secretKey)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String paramStr = "";
        List<String> sortedKeys = new ArrayList<String>(keyValues.size());
        sortedKeys.addAll(keyValues.keySet());
        Collections.sort(sortedKeys);
        for (int i = 0; i < sortedKeys.size(); i++) {
            String key = sortedKeys.get(i);
            String value = keyValues.get(key);
            if (null == key || "".equals(key) || null == value
                    || "".equals(value)) {
                continue;
            }
            paramStr += key + "=" + value;
        }
        String secretText = secretKey + "from=" + appKey + paramStr;
        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        crypt.reset();
        crypt.update(secretText.getBytes("UTF-8"));
        String sign = byteToHex(crypt.digest());
        return sign;
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
