package com.xiaoxian.service;

import com.xiaoxian.dao.OrderDao;
import com.xiaoxian.dao.StockDao;
import com.xiaoxian.entity.Stock;
import com.xiaoxian.entity.StockOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by XiaoXian on 2020/11/29.
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService{

    @Autowired
    private StockDao stockDao;

    @Autowired
    private OrderDao orderDao;

    @Override
    public int kill(Integer id) {
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
}
