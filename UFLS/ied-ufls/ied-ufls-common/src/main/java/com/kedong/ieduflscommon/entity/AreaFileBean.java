package com.kedong.ieduflscommon.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接收文件内容报文实体
 * 区域模块
 * 2022年12月26日14:08:16
 * mr_qin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaFileBean {
    //区域简称
    @JSONField(ordinal = 1,name="RegionID")
    private String regionID;

    //区域名称
    @JSONField(ordinal = 2,name="RegionName")
    private String regionName;

}
