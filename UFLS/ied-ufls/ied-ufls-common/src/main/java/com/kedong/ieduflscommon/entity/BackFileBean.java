package com.kedong.ieduflscommon.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接收文件内容结束后返回报文实体
 * 2022年12月26日15:32:44
 * mr_qin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackFileBean {
    //报文头信息 头信息,用来区别同一次传输的多个消息段
    @JSONField(ordinal = 1,name="HeadInfo")
    private String headInfo;

    //文件数据类型  MODEL：模型数据      DATA：实时数据
    @JSONField(ordinal = 2,name="DataType")
    private String dataType;

    //文件功能数据类型  (ROUND : 轮次)/ (UNIT : 装置)/ (ACTITEM : 动作项)/ (REPORT: 动作报告记录)(当datatype 为DATA时有意义)
    @JSONField(ordinal = 3,name="OpType")
    private String opType;

    //文件名称
    @JSONField(ordinal = 4,name="FileName")
    private String fileName;

    //响应时间戳
    @JSONField(ordinal = 5,name="AckTime")
    private long ackTime;

    //区域信息
    @JSONField(ordinal = 6,name="Condition")
    private AreaFileBean condition;

    //接收报文结果  0:成功，1:失败
    @JSONField(ordinal = 7,name="Result")
    private int result;
}
