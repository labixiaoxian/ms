package com.xiaoxian.dao;

import com.xiaoxian.entity.User;

/**
 * Created by XiaoXian on 2021/1/8.
 */
public interface UserDao {
    User findById(Integer id);
}
