package com.xiaoxian.service;

import com.xiaoxian.dao.OrderDao;
import com.xiaoxian.dao.StockDao;
import com.xiaoxian.dao.UserDao;
import com.xiaoxian.entity.Stock;
import com.xiaoxian.entity.StockOrder;
import com.xiaoxian.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by XiaoXian on 2020/11/29.
 */
@Service
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService{

    @Autowired
    private StockDao stockDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public int kill(Integer id) {

        //redis检验抢购时间
        if (!redisTemplate.hasKey("kill" + id)) {
            throw new RuntimeException("秒杀超时，活动已经结束啦");
        }

        //检验库存
        Stock stock = checkStock(id);
        //扣库存
        updateSale(stock);
        //创建订单
        return createOrder(stock);
    }

    @Override
    public String getMd5(Integer id, Integer userId) {
        //检验用户的合法性
        User user = userDao.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户信息不存在！");
        }
        log.info("用户信息：[{}]", user.toString());

        //检验商品的合法性
        Stock stock = stockDao.checkStock(id);
        if (stock == null) {
            throw new RuntimeException("商品信息不存在！");
        }
        log.info("商品信息：[{}]", stock.toString());

        //生成hashkey
        String hashKey = "KEY_" + userId + "_" + id;
        //生成md5
        String key = DigestUtils.md5DigestAsHex((userId + id + getSalt(4)).getBytes());
        redisTemplate.opsForValue().set(hashKey, key, 120, TimeUnit.SECONDS);
        log.info("redis写入：[{}][{}]",hashKey,key);
        return key;
    }

    @Override
    public int kill(Integer id, Integer userId, String md5) {

       /* //redis检验抢购时间
        if (!redisTemplate.hasKey("kill" + id)) {
            throw new RuntimeException("秒杀超时，活动已经结束啦");
        }*/

       //验证签名
        String hashKey = "KEY_" + userId + "_" + id;
        String s = redisTemplate.opsForValue().get(hashKey);
        if (s == null){
            throw new RuntimeException("没有携带验证签名，请求不合法！");
        }
        if (!s.equals(md5)) {
            throw new RuntimeException("当前请求数据不合法，请重试！");
        }

        //检验库存
        Stock stock = checkStock(id);
        //扣库存
        updateSale(stock);
        //创建订单
        return createOrder(stock);
    }


    //检验库存
    private Stock checkStock(Integer id) {
        Stock stock = stockDao.checkStock(id);
        if (stock.getCount().equals(stock.getSale())){
            throw new RuntimeException("库存不足");
        }
        return stock;
    }

    //扣库存
    private void updateSale(Stock stock) {
        int updateRows = stockDao.updateSale(stock);
        if (updateRows == 0) {
            throw new RuntimeException("抢购失败，请重试");
        }
    }

    //创建订单
    private Integer createOrder(Stock stock) {
        StockOrder stockOrder = new StockOrder();
        stockOrder.setSid(stock.getId()).setName(stock.getName()).setCreateTime(new Date());
        orderDao.createOrder(stockOrder);
        return stockOrder.getId();
    }

    //生成随机盐
    private String getSalt(int n) {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01234567890!@#$%^&*()".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0;i <n;i++) {
            char index = chars[new Random().nextInt(chars.length)];
            stringBuilder.append(index);
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        for (double y=1.3;y >= -1.1; y-=0.1) {
            for (double x=-1.2;x<=1.2;x+=0.03) {
                if (Math.pow((x * x + y * y - 1.0), 3) - x * x * y * y * y <= 0.0) {
                    System.out.print("*");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
}
