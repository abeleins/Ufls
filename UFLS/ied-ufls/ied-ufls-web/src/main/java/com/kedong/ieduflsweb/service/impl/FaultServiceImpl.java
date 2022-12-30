package com.kedong.ieduflsweb.service.impl;


import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kedong.ieduflsweb.mapper.FaultDao;
import com.kedong.ieduflswebcommon.entity.FaultVo;
import com.kedong.ieduflswebcommon.service.FaultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FaultServiceImpl implements FaultService {
    @Autowired
    private FaultDao faultDao;

    @Override
    public String getSgFaultList() {
//        size = size == null ? 15 : size;
        PageHelper.startPage(0, 20);
        List<FaultVo> faultList = faultDao.getSgFaultList();
        PageInfo<FaultVo> pageInfo = new PageInfo<>(faultList);

        return JSON.toJSONString(pageInfo);
    }
}
