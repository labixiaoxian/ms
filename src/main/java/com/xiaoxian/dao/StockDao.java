package com.xiaoxian.dao;

import com.xiaoxian.entity.Stock;

/**
 * Created by XiaoXian on 2020/11/29.
 */
public interface StockDao {

    //根据商品id查询商品信息
    Stock checkStock(Integer id);

    //根据商品id更新商品销量
    int updateSale(Stock stock);
}
