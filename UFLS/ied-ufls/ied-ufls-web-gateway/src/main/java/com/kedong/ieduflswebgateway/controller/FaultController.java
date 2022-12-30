package com.kedong.ieduflswebgateway.controller;

import com.kedong.ieduflswebgateway.config.ServiceLocator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/fault")
@Api(tags = "5、6 直调故障 网省故障")
public class FaultController {
    @Autowired
    private ServiceLocator serviceLocator;


    @ApiOperation("直调厂站故障列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stID", value = "故障厂站ID", paramType = "query"),
            @ApiImplicitParam(name = "dev", value = "一次设备", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "当前页码 必需", paramType = "query", required = true),
            @ApiImplicitParam(name = "size", value = "每页显示条数", paramType = "query", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query")
    })
    @RequestMapping("/getSgFaultList")
//    public String getSgFaultList(@RequestParam Integer pageNum, @RequestParam Integer size, String stID, String dev, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") Date startTime, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") Date endTime) {
    public String getSgFaultList(@RequestParam Integer pageNum, @RequestParam Integer size, String stID, String dev, @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime, @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime) {
        //TODO 时间格式化有问题会报400，改成年月日就可以了。
        return serviceLocator.faultService().getSgFaultList();
    }

}
