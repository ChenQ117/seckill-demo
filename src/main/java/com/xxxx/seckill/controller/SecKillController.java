package com.xxxx.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wf.captcha.ArithmeticCaptcha;
import com.xxxx.seckill.config.AccessLimit;
import com.xxxx.seckill.exception.GlobalException;
import com.xxxx.seckill.pojo.Order;
import com.xxxx.seckill.pojo.SeckillMessage;
import com.xxxx.seckill.pojo.SeckillOrder;
import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.rabbitmq.MQSender;
import com.xxxx.seckill.service.IGoodsService;
import com.xxxx.seckill.service.IOrderService;
import com.xxxx.seckill.service.ISeckillOrderService;
import com.xxxx.seckill.utils.JsonUtil;
import com.xxxx.seckill.vo.GoodsVo;
import com.xxxx.seckill.vo.RespBean;
import com.xxxx.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @version v1.0
 * @ClassName: SeckillController
 * @Description: 秒杀
 * @Author: ChenQ
 * @Date: 2022/9/7 on 11:05
 */
@Controller
@Slf4j
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;

    private Map<Long,Boolean> emptyStockMap = new HashMap<>();

    @RequestMapping(value = "/doSeckill2")
    private String doSeckill2(Model model, User user,Long goodsId){
        if (user == null){
            return "login";
        }
        model.addAttribute("user",user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if (goodsVo.getStockCount()<1){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }
        //判断是否重复抢购
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (seckillOrder!=null){
            model.addAttribute("errmsg",RespBeanEnum.REPEATE_ERROR.getMessage());
            return "secKillFail";
        }
        Order order = orderService.secKill(user, goodsVo);
        model.addAttribute("order",order);
        model.addAttribute("goods",goodsVo);
        return "orderDetail";
    }
    @RequestMapping(value = "/doSeckill3",method = RequestMethod.POST)
    @ResponseBody
    private RespBean doSeckill3(Model model, User user, Long goodsId){
        if (user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
//        model.addAttribute("user",user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if (goodsVo.getStockCount()<1){
//            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //判断是否重复抢购
//        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder!=null){
//            model.addAttribute("errmsg",RespBeanEnum.REPEATE_ERROR.getMessage());
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        Order order = orderService.secKill(user, goodsVo);
//        model.addAttribute("order",order);
//        model.addAttribute("goods",goodsVo);
        return RespBean.success(order);
    }

    @RequestMapping(value = "/{path}/doSeckill",method = RequestMethod.POST)
    @ResponseBody
    private RespBean doSeckill(@PathVariable String path, User user, Long goodsId){
        if (user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        //通过内存标记，减少redis的访问
        if (emptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        boolean check = orderService.checkPath(user,goodsId,path);
        if (!check){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder!=null){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }

        //预减库存 这个操作是原子的
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        if (stock<1){
            emptyStockMap.put(goodsId,true);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //把user和goodsVo转成字符串发送给前端，前端根据0显示抢购排队中
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0);
    }

    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
//    @AccessLimit(second=5,maxCount=5,needLogin=true)
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request){
        if (user==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        /*ValueOperations valueOperations = redisTemplate.opsForValue();
        //限制访问次数，5秒内最多允许访问5次
        String uri = request.getRequestURI();
        Integer count = (Integer) valueOperations.get(uri + ":" + user.getId());
        if (count == null){
            redisTemplate.opsForValue().set(uri+":"+user.getId(),1,5,TimeUnit.SECONDS);
        }else if (count<5){
            valueOperations.increment(uri+":"+user.getId());
        }else {
            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REACHED);
        }*/
        boolean check = orderService.checkCaptcha(user,goodsId,captcha);
        if (!check){
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        }
        String str = orderService.createPath(user,goodsId);
        return RespBean.success(str);
    }

    /**
     * 系统初始化，把商品库存数量加载到redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(list)){
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:"+goodsVo.getId(),goodsVo.getStockCount());
            emptyStockMap.put(goodsVo.getId(),false);
        });
    }

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return orderId:成功-1；失败0；排队中
     */
    @RequestMapping(value = "/getResult",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user,Long goodsId){
        if (user==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user,goodsId);
        //js的number只有16位，而Long是由mybatis-plus的雪花算法产生的id，有19位，必须把它转为字符串才能传递，否则会丢失精度
        //解决方案参考https://blog.csdn.net/Mr1ght/article/details/118178664
        return RespBean.success(orderId.toString());
    }

    /**
     * 生成验证码
     * @param user
     * @param goodsId
     * @param response
     */
    @RequestMapping("/captcha")
    public void verifyCode(User user, Long goodsId, HttpServletResponse response){
        if (null == user || goodsId<0){
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //设置请求头为输出图片的类型
        response.setContentType("image/jpg");
        response.setHeader("/Pargam","No-cache");
        response.setHeader("Cache-Control","no-cache");
        response.setDateHeader("Expires",0);
        //生成验证码。将结果放入Redis
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:"+user.getId()+":"+goodsId,captcha.text(),300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败",e.getMessage());
        }
    }
}
