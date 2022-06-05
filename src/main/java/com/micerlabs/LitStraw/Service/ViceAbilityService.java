package com.micerlabs.LitStraw.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ViceAbilityService {
    @Autowired
    private RedisTemplate redisTemplate;

    // 获取访问数量
    public int getVisitNum() {
        if (redisTemplate.hasKey("visitNum")) {
            return (int) redisTemplate.boundValueOps("visitNum").get();
        } else {
            redisTemplate.boundValueOps("visitNum").set(0);
            return 0;
        }
    }

    // 获取提取数量
    public int getExtractNum() {
        if (redisTemplate.hasKey("extractNum")) {
            return (int) redisTemplate.boundValueOps("extractNum").get();
        } else {
            redisTemplate.boundValueOps("extractNum").set(0);
            return 0;
        }
    }


    // 更新访问数量+1
    public void updateVisitNum() {
        if (redisTemplate.hasKey("visitNum")) {
            redisTemplate.boundValueOps("visitNum").set(getVisitNum() + 1);
        } else {
            redisTemplate.boundValueOps("visitNum").set(1);
        }
    }

    // 更新提取数量+1
    public void updateExtractNum() {
        if (redisTemplate.hasKey("extractNum")) {
            redisTemplate.boundValueOps("extractNum").set(getExtractNum() + 1);
        } else {
            redisTemplate.boundValueOps("extractNum").set(1);
        }
    }

}
