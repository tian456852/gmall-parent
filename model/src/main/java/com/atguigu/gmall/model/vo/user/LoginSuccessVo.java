package com.atguigu.gmall.model.vo.user;

import lombok.Data;

/**
 * @author tkwrite
 * @create 2022-09-07-0:14
 */
@Data
public class LoginSuccessVo {//整个对象会存到Cookie
    private String token; //用户的令牌
    private String nickName; //用户

}
