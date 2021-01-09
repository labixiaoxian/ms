package com.xiaoxian.service;

/**
 * Created by XiaoXian on 2021/1/9.
 */
public interface UserService {

    //向redis中写入用户访问次数
    int saveUserCount(Integer userId);

    //判断单位时间调用次数
    boolean getUserCount(Integer userId);
}
