package com.kedong.ieduflscommon.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接收文件内容报文实体
 * 2022年12月26日14:08:16
 * mr_qin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecFileBean {
    //报文头信息
    @JSONField(ordinal = 1,name="HeadInfo")
    private String headInfo;

    //当前切片序号
    @JSONField(ordinal = 2,name="No")
    private int no;

    //总切片数
    @JSONField(ordinal = 3,name="TotalNums")
    private int totalNums;

    //结束标志位 0：文件传输开始，1：文件传输中2：文件传输结束
    @JSONField(ordinal = 4,name="EndFlag")
    private int endFlag;

    //文件数据类型  MODEL：模型数据      DATA：实时数据
    @JSONField(ordinal = 5,name="DataType")
    private String dataType;

    //文件功能数据类型  (ROUND : 轮次)/ (UNIT : 装置)/ (ACTITEM : 动作项)/ (REPORT: 动作报告记录)(当datatype 为DATA时有意义)
    @JSONField(ordinal = 6,name="OpType")
    private String opType;

    //区域信息
    @JSONField(ordinal = 7,name="Condition")
    private AreaFileBean condition;

    //文件内容数据信息
    @JSONField(ordinal = 8,name="Data")
    private DataFileBean data;
}
