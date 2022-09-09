package com.xxxx.seckill.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version v1.0
 * @ClassName: SeckillMessage
 * @Description: 秒杀信息
 * @Author: ChenQ
 * @Date: 2022/9/9 on 11:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillMessage {
    private User user;
    private Long goodId;
}
