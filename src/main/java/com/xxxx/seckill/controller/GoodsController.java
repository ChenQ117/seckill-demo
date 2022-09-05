package com.xxxx.seckill.controller;

import com.xxxx.seckill.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpSession;

/**
 * @version v1.0
 * @ClassName: GoodsController
 * @Description:
 * @Author: ChenQ
 * @Date: 2022/9/5 on 21:35
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {
    @RequestMapping("/toList")
    public String toList(HttpSession session, Model model, @CookieValue("userTicket") String ticket){
        //如果未登陆，即cookie中没有用户信息则让重新登录
        if (StringUtils.isEmpty(ticket)){
            return "login";
        }
        User user = (User) session.getAttribute(ticket);
        if (null == user){
            return "login";
        }
        model.addAttribute("user",user);
        return "goodList";
    }
}
