package com.atguigu.gmall.user.api;

import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import com.atguigu.gmall.user.service.UserAddressService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author tkwrite
 * @create 2022-09-13-19:33
 */
@RequestMapping("/api/inner/rpc/user")
@RestController
public class UserApiController {

    @Autowired
    UserAddressService userAddressService;

    /**
     * 获取所有的收货地址
     * @return
     */
    @GetMapping("/address/list")
    public Result<List<UserAddress>>getUserAddressList(){
        //1.获取当前登录的用户
        UserAuthInfo info = AuthUtils.getCurrentAuthInfo();
        //2.获取所有用户id
        Long userId = info.getUserId();
        //3.获取所有收货地址
        LambdaQueryWrapper<UserAddress> wrapper = new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId, userId);
        List<UserAddress> userAddresses = userAddressService.list(wrapper);
        return Result.ok(userAddresses);
    }

}
