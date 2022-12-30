package com.kedong.ieduflsreceive.cache;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.concurrent.TimeUnit;

public class ValueOperations {
    //保存键值对，键值对会自动过期
    private ExpiringMap<String, String> cacheMap;
    //超时时间，单位分钟
    private Integer timeout = 60;

    public ValueOperations() {
        init();
    }

    public void init() {
        cacheMap = ExpiringMap.builder().expiration(timeout, TimeUnit.MINUTES)
                .expirationPolicy(ExpirationPolicy.CREATED)
                .maxSize(1024 * 50)
                .build();
    }

    public void set(String key, String value) {
        cacheMap.put(key, value);
    }

    public String get(String key) {
        return cacheMap.get(key);
    }

    public void del(String key){
        cacheMap.remove(key);
    }
}
