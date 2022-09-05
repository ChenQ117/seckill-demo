package com.xxxx.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @version v1.0
 * @ClassName: RespBeanEnum
 * @Description: 公用返回对象枚举类
 * @Author: ChenQ
 * @Date: 2022/9/5 on 10:20
 */
@Getter
@ToString
@AllArgsConstructor
public enum  RespBeanEnum {
    //通用
    SUCCESS(200,"SUCCESS"),
    ERROR(500,"服务端异常"),

    //登录模块
    LOGIN_ERROR(500210,"用户名或密码不正确"),
    MOBILE_ERROR(500211,"手机号码格式不正确"),
    BIND_ERROR(500212,"参数校验异常")
    ;
    private final Integer code;
    private final String message;
}
