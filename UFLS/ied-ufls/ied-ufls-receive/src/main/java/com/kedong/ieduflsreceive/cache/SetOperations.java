package com.kedong.ieduflsreceive.cache;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SetOperations {
    //保存Set集合，不会自动过期，注意使用完之后删除用完的元素
    private ConcurrentMap<String, Set<String>> setMap;

    public SetOperations() {
        init();
    }

    public void init() {
        setMap = new ConcurrentHashMap<>();
    }

    public Long add(String key, String value) {
        Set<String> member = members(key);
        if (member == null)
            member = new HashSet<>();
        boolean add = member.add(value);
        return add ? 1L : 0L;
    }

    public Set<String> members(String key) {
        Set<String> set = setMap.get(key);
        return set;
    }

    public void remove(String key, String value) {
        Set<String> member = members(key);
        if (member == null)
            return;
        member.remove(value);
    }
}
