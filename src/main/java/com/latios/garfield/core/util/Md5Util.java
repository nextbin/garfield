package com.latios.garfield.core.util;

import org.apache.log4j.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author zebin
 * @since 2016-10-05.
 */
public class Md5Util {
    private static final Logger LOG = Logger.getLogger(Md5Util.class);

    public static String md5(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(str.getBytes());
            StringBuilder md5str = new StringBuilder("");
            for (byte digit : bytes) {
                md5str.append(Integer.toHexString((digit >>> 4) & 15));
                md5str.append(Integer.toHexString(digit & 15));
            }
            return md5str.toString();
        } catch (NoSuchAlgorithmException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }
}
