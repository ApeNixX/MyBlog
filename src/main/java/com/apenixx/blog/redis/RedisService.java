package com.apenixx.blog.redis;

/**
 * @Author ApeNixX
 * @Date 2020/2/2 23:58
 * @Version 1.0
 * @Describe
 */
public interface RedisService {

    /**
     * 判断key是否存在
     */
    Boolean hasKey(String key);
}
