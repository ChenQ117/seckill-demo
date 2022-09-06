package com.xxxx.seckill.vo;

import com.xxxx.seckill.pojo.Goods;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @version v1.0
 * @ClassName: GoodsVo
 * @Description:
 * @Author: ChenQ
 * @Date: 2022/9/6 on 22:40
 */

@Data
public class GoodsVo extends Goods {

    /**
     * 秒杀价
     */
    private BigDecimal seckillPrice;

    /**
     * 库存数量
     */
    private Integer stockCount;

    /**
     * 秒杀开始时间
     */
    private Date startDate;

    /**
     * 秒杀结束时间
     */
    private Date endDate;
}
