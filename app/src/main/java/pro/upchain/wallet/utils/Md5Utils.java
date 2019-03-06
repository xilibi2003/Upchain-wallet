package pro.upchain.wallet.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 使用md5的算法进行加密的Utils
 */
public class Md5Utils {



    public static String md5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    public static void main(String[] args) {

        System.out.println(md5("123"));
    }

    public static String a(String var0) {
        try {
            MessageDigest var1 = MessageDigest.getInstance("MD5");
            var1.update(var0.getBytes());
            return b(var1.digest());
        } catch (NoSuchAlgorithmException var2) {
            var2.printStackTrace();
            return "";
        }
    }

    public static String a(byte[] var0) {
        try {
            MessageDigest var1 = MessageDigest.getInstance("MD5");
            var1.update(var0);
            return b(var1.digest());
        } catch (NoSuchAlgorithmException var2) {
            var2.printStackTrace();
            return "";
        }
    }

    private static String b(byte[] var0) {
        StringBuffer var1 = new StringBuffer(var0.length * 2);

        for(int var2 = 0; var2 < var0.length; ++var2) {
            var1.append(Character.forDigit((var0[var2] & 240) >> 4, 16));
            var1.append(Character.forDigit(var0[var2] & 15, 16));
        }

        return var1.toString();
    }

}


