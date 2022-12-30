package com.kedong.ieduflsreceive.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 解析文件处理文件数据接口
 * 2022年12月29日14:44:00
 * mr_qin
 */
public interface DataService {
    /**
     * 解析装置模型文件
     * 入库装置数据、信号数据
     * @param filePath 文件绝对路径
     * @return
     */
    boolean analyModelUnit(String filePath);
}
