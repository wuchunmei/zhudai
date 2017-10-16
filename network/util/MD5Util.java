package com.zhudai.common.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

public class MD5Util {
    public final static String MD5(String s) {
        return MD5(s, null);
    }

    public final static String MD5(String s, String charsetname) {
        byte[] btInput = null;
        if (charsetname == null) {
            btInput = s.getBytes();
        } else {
            try {
                btInput = s.getBytes(charsetname);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        }
        return MD5(btInput);
    }

    public final static String MD5(byte[] data) {
        try {
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(data);
            byte[] md = mdInst.digest();

            return toHexString(md);
        } catch (Exception e) {
            return null;
        }
    }

    public final static String toHexString(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int val = data[i] & 0xff;
            if (val < 16)
                sb.append("0");
            sb.append(Integer.toHexString(val));

        }
        return sb.toString();
    }
}
