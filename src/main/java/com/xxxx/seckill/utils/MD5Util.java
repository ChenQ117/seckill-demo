package com.xxxx.seckill.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

/**
 * @version v1.0
 * @ClassName: MD5Util
 * @Description: MD5加密工具类
 * @Author: ChenQ
 * @Date: 2022/9/5 on 8:41
 */
//@Component
public class MD5Util {
    //md5加密
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1a2b3c4d";
    //第一次加密
    public static String inputPassToFromPass(String inputPass){
        //混淆salt
        String str = ""+salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }
    //第二次加密
    public static String fromPassToDBPass(String fromPass,String salt){
        String str = ""+salt.charAt(0) + salt.charAt(2) + fromPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDBPass(String inputPass,String salt){
        String fromPass = inputPassToFromPass(inputPass);
        String dbPass = fromPassToDBPass(fromPass,salt);
        return dbPass;
    }

    public static void main(String[] args) {
        String inputPass = inputPassToFromPass("123456");
        System.out.println(inputPass);
        String dbPass = fromPassToDBPass(inputPass,"1a2b3c4d");
        System.out.println(dbPass);
        String s = inputPassToDBPass("123456", "1a2b3c4d");
        System.out.println(s);
    }
}
