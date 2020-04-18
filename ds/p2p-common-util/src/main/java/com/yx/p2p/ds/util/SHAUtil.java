package com.yx.p2p.ds.util;

import java.security.MessageDigest;

/**
 * @description:
 * 参考内容：
 * 7.SHA(Secure Hash Algorithm，安全散列算法)
 * JAVA各种加密与解密方式
 * https://blog.csdn.net/theUncle/article/details/100156976#7.SHA(Secure%20Hash%20Algorithm%EF%BC%8C%E5%AE%89%E5%85%A8%E6%95%A3%E5%88%97%E7%AE%97%E6%B3%95)
 * @author: yx
 * @date: 2020/04/15/16:09
 */
public class SHAUtil {
    private static final String KEY_SHA = "SHA";


    public static String encryptSHA(Object obj) {
        try {
            MessageDigest sha = MessageDigest.getInstance(KEY_SHA);
            byte[] sha_byte = sha.digest(ObjectUtil.objToByteArray(obj));
            StringBuffer hexValue = new StringBuffer();
            for (byte b : sha_byte) {
                //将其中的每个字节转成十六进制字符串：byte类型的数据最高位是符号位，通过和0xff进行与操作，转换为int类型的正整数。
                String toHexString = Integer.toHexString(b & 0xff);
                hexValue.append(toHexString.length() == 1 ? "0" + toHexString : toHexString);
            }
            return hexValue.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String encryptSHA( String content) {
        try {
            MessageDigest sha = MessageDigest.getInstance(KEY_SHA);
            byte[] sha_byte = sha.digest(content.getBytes());
            StringBuffer hexValue = new StringBuffer();
            for (byte b : sha_byte) {
                //将其中的每个字节转成十六进制字符串：byte类型的数据最高位是符号位，通过和0xff进行与操作，转换为int类型的正整数。
                String toHexString = Integer.toHexString(b & 0xff);
                hexValue.append(toHexString.length() == 1 ? "0" + toHexString : toHexString);
            }
            return hexValue.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /***
     * SHA加密（比MD5更安全）
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] encryptSHA(byte[] data) throws Exception{
        MessageDigest sha = MessageDigest.getInstance(KEY_SHA);
        sha.update(data);
        return sha.digest();
    }


}
