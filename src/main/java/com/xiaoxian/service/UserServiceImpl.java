package com.xiaoxian.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * @author 小贤
 * @PackageName:com.xiaoxian.service
 * @ClassName:UserServiceImpl
 * @Description:
 * @data 2021/1/9 16:14
 */
@Service
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public int saveUserCount(Integer userId) {
        String limitKey = "LIMIT" + "_" + userId;
        String limitNum = stringRedisTemplate.opsForValue().get(limitKey);
        int limit = 0;
        if (limitNum == null) {
            //第一次调用放入redis中设置为0
            stringRedisTemplate.opsForValue().set(limitKey, "0", 3600, TimeUnit.SECONDS);
        } else {
            limit = Integer.parseInt(limitNum) + 1;
            stringRedisTemplate.opsForValue().set(limitKey,String.valueOf(limit),3600,TimeUnit.SECONDS);
        }
        return limit;
    }

    @Override
    public boolean getUserCount(Integer userId) {
        //根据userid对应key获取调用次数
        String limitKey = "LIMIT" + "_" + userId;
        String limitNum = stringRedisTemplate.opsForValue().get(limitKey);
        if (limitNum == null) {
            log.error("该用户没有访问申请验证值记录，疑似异常");
            return true;
        }
        return Integer.parseInt(limitNum) > 10; //false代表没有超过  true代表超过
    }

}
