package com.xxxx.seckill.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @version v1.0
 * @ClassName: RespBean
 * @Description: 公用返回对象
 * @Author: ChenQ
 * @Date: 2022/9/5 on 10:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespBean {
    private long code;
    private String message;
    private Object obj;

    /**
     * 成功的返回结果
     * @return
     */
    public static RespBean success(){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(),RespBeanEnum.SUCCESS.getMessage(),null);
    }
    public static RespBean success(Object o){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(),RespBeanEnum.SUCCESS.getMessage(),o);
    }

    /**
     * 失败的返回结果
     * @param respBeanEnum
     * @return
     */
    public static RespBean error(RespBeanEnum respBeanEnum){
        return new RespBean(respBeanEnum.getCode(),respBeanEnum.getMessage(),null);
    }
    public static RespBean error(RespBeanEnum respBeanEnum,Object o){
        return new RespBean(respBeanEnum.getCode(),respBeanEnum.getMessage(),o);
    }
}
