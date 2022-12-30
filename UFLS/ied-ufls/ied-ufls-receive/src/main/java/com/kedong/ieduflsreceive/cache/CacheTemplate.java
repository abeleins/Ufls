package com.kedong.ieduflsreceive.cache;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * 本地缓存类
 * 由于本程序使用的缓存不大，大可不必使用Redis增加代码的复杂度，故实现本地缓存来实现部分redis的功能
 * 用以替代RedisTemplate来实现本地缓存
 * 方法参考RedisTemplate中部分方法
 *
 * @author wangxiangsheng
 */
@Log4j2
@Component
public class CacheTemplate {
    //保存键值对
    private ValueOperations valueOperations;
    //保存set
    private SetOperations setOperations;

    public CacheTemplate() {
        valueOperations = new ValueOperations();
        setOperations = new SetOperations();
        log.info("本地缓存初始化完成");
    }


    public SetOperations opsForSet() {
        return setOperations;
    }

    public ValueOperations opsForValue() {
        return valueOperations;
    }

    public void delete(String key) {
        opsForValue().del(key);
    }
}
