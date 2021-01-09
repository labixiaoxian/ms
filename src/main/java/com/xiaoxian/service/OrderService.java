package com.xiaoxian.service;

/**
 * Created by XiaoXian on 2020/11/29.
 */
public interface OrderService {
    int kill(Integer id);

    String getMd5(Integer id, Integer userId);

    int kill(Integer id, Integer userId, String md5);
}
