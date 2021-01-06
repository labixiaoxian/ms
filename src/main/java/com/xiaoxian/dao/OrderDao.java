package com.xiaoxian.dao;

import com.xiaoxian.entity.StockOrder;

/**
 * Created by XiaoXian on 2021/1/5.
 */
public interface OrderDao {

    //创建订单
    int createOrder(StockOrder stockOrder);
}
