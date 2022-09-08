package com.xxxx.seckill.controller;

import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.service.IGoodsService;
import com.xxxx.seckill.service.IUserService;
import com.xxxx.seckill.vo.DetailVo;
import com.xxxx.seckill.vo.GoodsVo;
import com.xxxx.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * windows优化前954   对象缓存后2085
 * Linux优化前100
 * @version v1.0
 * @ClassName: GoodsController
 * @Description:
 *
 * @Author: ChenQ
 * @Date: 2022/9/5 on 21:35
 */
@Controller
@RequestMapping("/goods")
@Slf4j
public class GoodsController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;
    @RequestMapping(value = "/toList",produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(Model model, User user,HttpServletRequest request,HttpServletResponse response){
        //如果未登陆，即cookie中没有用户信息则让重新登录
//        if (StringUtils.isEmpty(ticket)){
//            return "login";
//        }
////        User user = (User) session.getAttribute(ticket);
//        User user = userService.getUserByCookie(ticket, request, response);
//        if (null == user){
//            return "login";
//        }
        //Redis中获取页面，如果不为空，直接访问页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if (!StringUtils.isEmpty(html)){
            return html;
        }
        model.addAttribute("user",user);
        model.addAttribute("goodsList",goodsService.findGoodsVo());
//        return "goodsList";
        //如果为空，则手动渲染，存入Redis并返回
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
        if (!StringUtils.isEmpty(html)){
            valueOperations.set("goodsList",html,60, TimeUnit.SECONDS);
        }
        return html;
    }

    @RequestMapping(value = "/toDetail/{goodsId}",produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail2(Model model, User user,@PathVariable Long goodsId,HttpServletRequest request,HttpServletResponse response){
        //如果未登陆，即cookie中没有用户信息则让重新登录

        ValueOperations valueOperations = redisTemplate.opsForValue();
        //Redis中获取页面
        String html = (String) valueOperations.get("goodsDetail:" + goodsId);
        if (!StringUtils.isEmpty(html)){
            return html;
        }
        model.addAttribute("user",user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        //秒杀状态
        int secKillStatus = 0;
        int remainSeconds = 0;
        if (nowDate.before(startDate)){
            //秒杀还没开始
            remainSeconds = (int) ((startDate.getTime()-nowDate.getTime())/1000);

        }else if (nowDate.after(endDate)){
            //秒杀已经结束
            secKillStatus = 2;
        }else {
            secKillStatus = 1;
        }
        model.addAttribute("secKillStatus",secKillStatus);
        model.addAttribute("remainSeconds",remainSeconds);
        model.addAttribute("goods",goodsVo);
//        return "goodsDetail";

        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail",webContext);
        if (!StringUtils.isEmpty(html)){
            valueOperations.set("goodsDetail:"+goodsId,html,60,TimeUnit.SECONDS);
        }
        return html;
    }

    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(Model model, User user,@PathVariable Long goodsId){
        //如果未登陆，即cookie中没有用户信息则让重新登录

        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        //秒杀状态
        int secKillStatus = 0;
        int remainSeconds = 0;
        if (nowDate.before(startDate)){
            //秒杀还没开始
            remainSeconds = (int) ((startDate.getTime()-nowDate.getTime())/1000);

        }else if (nowDate.after(endDate)){
            //秒杀已经结束
            secKillStatus = 2;
        }else {
            secKillStatus = 1;
        }
        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSecKillStatus(secKillStatus);
        detailVo.setRemainSeconds(remainSeconds);

        return RespBean.success(detailVo);

    }

}
