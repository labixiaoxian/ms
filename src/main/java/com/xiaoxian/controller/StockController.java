package com.xiaoxian.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.xiaoxian.service.OrderService;
import com.xiaoxian.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * Created by XiaoXian on 2020/11/29.
 */
@RestController
@RequestMapping("/stock")
@Slf4j
public class StockController {


    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    private RateLimiter rateLimiter = RateLimiter.create(20);


    @RequestMapping("/md5")
    public String getMd5(Integer id, Integer userId) {
        String md5;
        try {
            md5 = orderService.getMd5(id, userId);
        } catch (Exception e) {
            e.printStackTrace();
            return "获取MD5失败:"+e.getMessage();
        }
        return "获取MD5信息：" + md5;
    }

    @GetMapping("/sale")
    public String sale(Integer id) {
        //1.如果没有获取到token，会一直等待，直到获取到token
        //log.info("等待时间：" + rateLimiter.acquire());

        //2.设置一等时间，如果在该时间范围内获取到token，则处理业务。如果在时间范围内获取不到token,则抛弃
        if (!rateLimiter.tryAcquire(2, TimeUnit.SECONDS)) {
            System.out.println("当前请求背限流，直接抛弃，无法进行秒杀业务");
            return "抢购失败";
        }

        System.out.println("处理业务。。。。。。。");
        return "抢购成功";
    }

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

    @GetMapping("/killtoken")
    public String killtoken(Integer id) {
        System.out.println("秒杀商品的id = " + id);
        if (!rateLimiter.tryAcquire(2, TimeUnit.SECONDS)) {
            log.info("抛弃请求：抢购失败，当前秒杀活动过于火爆，请重试");
            return "抛弃请求：抢购失败，当前秒杀活动过于火爆，请重试";
        }
        try {
            int orderId = orderService.kill(id);
            return "秒杀成功，订单id为："+String.valueOf(orderId);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @GetMapping("/killtokenmd5")
    public String killtokenmd5(Integer id,Integer userId,String md5) {
        System.out.println("秒杀商品的id = " + id);
        if (!rateLimiter.tryAcquire(2, TimeUnit.SECONDS)) {
            log.info("抛弃请求：抢购失败，当前秒杀活动过于火爆，请重试");
            return "抛弃请求：抢购失败，当前秒杀活动过于火爆，请重试";
        }
        try {
            int orderId = orderService.kill(id,userId,md5);
            return "秒杀成功，订单id为："+String.valueOf(orderId);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @GetMapping("/killtokenmd5limit")
    public String killtokenmd5limit(Integer id,Integer userId,String md5) {
        //加入令牌桶的限流措施
        if (!rateLimiter.tryAcquire(2, TimeUnit.SECONDS)) {
            log.info("抛弃请求：抢购失败，当前秒杀活动过于火爆，请重试");
            return "抛弃请求：抢购失败，当前秒杀活动过于火爆，请重试";
        }
        try {
            //单用户调用接口的频率限制
            int count = userService.saveUserCount(userId);
            log.info("用户截至该次的访问次数为：[{}]", count);
            //进行调用次数判断
            boolean isBanned = userService.getUserCount(userId);
            if (isBanned) {
                log.info("购买失败，超过频率限制！");
                return "购买失败，超过频率限制！";
            }
            //根据秒杀商品id去调用秒杀业务
            int orderId = orderService.kill(id,userId,md5);
            return "秒杀成功，订单id为："+String.valueOf(orderId);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
