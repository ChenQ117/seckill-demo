package com.xxxx.seckill.utils;

import org.thymeleaf.util.StringUtils;

import java.util.regex.Pattern;

/**
 * @version v1.0
 * @ClassName: ValidatorUtil
 * @Description: 手机号码校验
 * @Author: ChenQ
 * @Date: 2022/9/5 on 11:52
 */
public class ValidatorUtil {
    private static final Pattern mobile_pattern = Pattern.compile("[1]([3-9])[0-9]{9}$");

    /**
     * 验证手机号是否合法
     * @param mobile
     * @return
     */
    public static boolean isMobile(String mobile){
        if (StringUtils.isEmpty(mobile)){
            return false;
        }
        return mobile_pattern.matcher(mobile).matches();
    }
}
