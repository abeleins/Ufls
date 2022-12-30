package com.kedong.ieduflswebcommon.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@ApiModel(description = "故障列表视图")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaultVo implements Serializable {
    @ApiModelProperty(value = "故障ID")
    private String faultID;
    @ApiModelProperty(value = "一次设备名字")
    private String faultSecdev;
    @ApiModelProperty(value = "故障名称")
    private String name;
    @ApiModelProperty(value = "故障时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Timestamp faultTime;
    @ApiModelProperty(value = "故障描述")
    private String descr;
    @ApiModelProperty(value = "故障厂站A")
    private String subA;
    @ApiModelProperty(value = "故障厂站B")
    private String subB;
}
