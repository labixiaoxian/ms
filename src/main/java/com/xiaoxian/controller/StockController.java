package com.xiaoxian.controller;

import com.xiaoxian.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by XiaoXian on 2020/11/29.
 */
@RestController
@RequestMapping("/stock")
public class StockController {


    @Autowired
    private OrderService orderService;

    @GetMapping("/kill")
    public String kill(Integer id){
        System.out.println("秒杀商品的id = " + id);
        //调用秒杀业务
        try {
            //悲观锁解决超卖
            /*synchronized (this) {
                int orderId = orderService.kill(id);
                return "秒杀成功，订单id为："+String.valueOf(orderId);
            }*/

            //乐观锁解决超卖——version字段与数据库的事务
            int orderId = orderService.kill(id);
            return "秒杀成功，订单id为："+String.valueOf(orderId);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
