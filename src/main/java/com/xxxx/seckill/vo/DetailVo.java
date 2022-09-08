package com.xxxx.seckill.vo;

import com.xxxx.seckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version v1.0
 * @ClassName: DetailVo
 * @Description: 详情返回对象
 * @Author: ChenQ
 * @Date: 2022/9/8 on 12:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailVo {
    private User user;
    private GoodsVo goodsVo;
    private int secKillStatus;
    private int remainSeconds;
}
