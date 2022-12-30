package com.kedong.ieduflswebcommon.service;

public interface BaseService {
    /**
     * 保护设备数组的顺序 按照 线路保护，变压器保护，发变组保护，母线保护，断路器保护，并联电抗器保护，并联电容器保护，串联电抗器保护，串联电容器保护 顺序
     */
    //通用表前缀
    String[] prefixTables = new String[]{"SG_PRT_LINE", "SG_PRT_TRANS", "SG_PRT_GENTRANS", "SG_PRT_BUS", "SG_PRT_BREAK", "SG_PRT_SHUNTRA", "SG_PRT_SHUNTCP", "SG_PRT_SERIESRA", "SG_PRT_SERIESCP"};
    //    与一次设备关联的字段名
    String[] linkedColumn = new String[]{"lineter_id", "trans_id", "gentrans_id", "bus_id", "break_id", "shuntra_id", "shuntcp_id", "seriesra_id", "seriescp_id"};
    //    一次设备表名
    String[] firstTable = new String[]{"rt_dev_aclineend", "rt_dev_pwrtransfm", "rt_dev_transfmwd", "rt_dev_busbar", "rt_dev_breaker", "rt_dev_shuntreactor", "rt_dev_shuntcapacitor", "rt_dev_seriesreactor", "rt_dev_seriescapacitor"};

    //厂站表名 发电厂、变电站、换流站
    String[] subNames = {"rt_con_plant", "rt_con_substation", "rt_con_conversubstation"};
    //厂站表号
    String[] subNo = {"0111", "0112", "0113"};
    /**
     * 模式名在每个持久层方法中都得传入，日后模式名如有修改仅需要修改此处
     */
    //保护设备模式
    String modelName = "sg_prt";

    //其他表（厂站，电压，一次设备等）的模式
    String onlineModel = "scm_psdb_online";

    //厂站地区前缀，如想表示具体地区，可以在字符串后面拼接一个数 华北1，华东2，华中3，东北4，西北5，西南6，如华北--0101990101
    String area = "010199010";

}
