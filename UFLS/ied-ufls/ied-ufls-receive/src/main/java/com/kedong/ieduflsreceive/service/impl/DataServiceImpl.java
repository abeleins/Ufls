package com.kedong.ieduflsreceive.service.impl;

import com.kedong.ieduflsreceive.service.DataService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

/**
 * 解析文件处理文件数据服务
 * 2022年12月29日14:44:00
 * mr_qin
 */
@Service
@EnableScheduling
@EnableAsync
public class DataServiceImpl implements DataService {

    /**
     * 解析装置模型文件
     * 入库装置数据、信号数据
     * @param filePath
     * @return
     */
    @Override
    public boolean analyModelUnit(String filePath) {

        return false;
    }
}
