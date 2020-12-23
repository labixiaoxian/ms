package com.xiaoxian.service;

import com.xiaoxian.dao.StockDao;
import com.xiaoxian.entity.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by XiaoXian on 2020/11/29.
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService{

    @Autowired
    private StockDao stockDao;

    @Override
    public int kill(Integer id) {
        //根据商品id校验库存
        Stock stock = stockDao.checkStock(id);
        if (stock.getCount().equals(stock.getSale())){
            throw new RuntimeException("库存不足");
        }else {
            //扣库存
            //创建订单
        }
        return 0;
    }
}
