package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.service.PaymentInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tkwrite
 * @create 2022-09-13-21:14
 */
@RequestMapping("/api/order/auth")
@RestController
public class OrderRestController {

    @Autowired
    OrderBizService orderBizService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    OrderInfoService orderInfoService;
    @Autowired
    OrderDetailService orderDetailService;

    /**
     * 提交订单
     * @return
     */
    @PostMapping("/submitOrder")
    public Result submitOrder(@RequestParam("tradeNo") String tradeNo,
                              @RequestBody OrderSubmitVo submitVo){
       Long orderId= orderBizService.submitOrder(submitVo,tradeNo);
        return Result.ok(orderId.toString());
    }

    @GetMapping("/{pn}/{ps}")
    public Result orderList(@PathVariable("ps")Long pn,@PathVariable("ps")Long ps){
        Long userId = AuthUtils.getCurrentAuthInfo().getUserId();
        Page<OrderInfo> page=new Page<>(ps,pn);
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getUserId, userId);
        //1.查询orderInfo
        Page<OrderInfo> infoPage = orderInfoService.page(page, wrapper);

        //2.查询orderInfo的所有商品
        infoPage.getRecords().stream()
                .parallel()
                .forEach(orderInfo -> {
                    List<OrderDetail> orderDetails = orderDetailService.getOrderDetails(orderInfo.getId(), orderInfo.getUserId());
                    orderInfo.setOrderDetailList(orderDetails);
                });
        return Result.ok(infoPage);

    }

}
