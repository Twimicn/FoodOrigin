package com.moe.fo.util;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * 生成MD5的值
 */
public class MD5 {
    public static String encode(String str) {
        if (str == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
