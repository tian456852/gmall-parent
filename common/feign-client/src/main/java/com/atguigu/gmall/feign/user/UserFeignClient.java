package com.atguigu.gmall.feign.user;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author tkwrite
 * @create 2022-09-13-19:40
 */
@RequestMapping("/api/inner/rpc/user")
@FeignClient("service-user")
public interface UserFeignClient {
    /**
     * 获取所有的收货地址
     * @return
     */
    @GetMapping("/address/list")
    Result<List<UserAddress>> getUserAddressList();

}
