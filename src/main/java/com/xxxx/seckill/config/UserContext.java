package com.xxxx.seckill.config;

import com.xxxx.seckill.pojo.User;

/**
 * @version v1.0
 * @ClassName: UserContext
 * @Description:
 * @Author: ChenQ
 * @Date: 2022/9/9 on 16:28
 */
public class UserContext {
    private static ThreadLocal<User> userHolder = new ThreadLocal<>();
    public static void setUser(User user){
        userHolder.set(user);
        System.out.println("setUser:"+user);
    }
    public static User getUser(){
        User user = userHolder.get();
        System.out.println("getUser:"+user);
        return user;
    }
}
