package com.kedong.ieduflscommon.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接收文件内容报文实体
 * 内容
 * 2022年12月26日14:08:16
 * mr_qin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataFileBean {
    //文件名称
    @JSONField(ordinal = 1,name="FileName")
    private String fileName;

    //文件内容总长度
    @JSONField(ordinal = 2,name="TotalLen")
    private Long totalLen;

    //当前片长度
    @JSONField(ordinal = 3,name="CurLen")
    private Long curLen;

    //文件生成时间  时间戳
    @JSONField(ordinal = 4,name="Time")
    private Long time;

    //文件内容数据
    @JSONField(ordinal = 5,name="BinData")
    private String binData;
}
