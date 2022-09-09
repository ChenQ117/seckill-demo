package com.xxxx.seckill.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author cq
 * @since 2022-09-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_seckill_order")
public class SeckillOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;
    /**
     * 用户ID
     */
    @JsonSerialize(using= ToStringSerializer.class)
    private Long userId;

    /**
     * 订单ID
     */
    @JsonSerialize(using= ToStringSerializer.class)
    private Long orderId;

    /**
     * 商品ID
     */
    @JsonSerialize(using= ToStringSerializer.class)
    private Long goodsId;


}
