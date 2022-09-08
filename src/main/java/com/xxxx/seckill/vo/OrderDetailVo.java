package com.xxxx.seckill.vo;

import com.xxxx.seckill.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version v1.0
 * @ClassName: OrderDetailVo
 * @Description: 订单详情返回对象
 * @Author: ChenQ
 * @Date: 2022/9/8 on 13:23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVo {
    private Order order;
    private GoodsVo goodsVo;
}
