package com.kedong.ieduflscommon.enums;

/**
 * 数据类型
 * 2022年12月28日16:37:48
 * mr_qin
 */
public enum DataType {
    MODEL("MODEL","模型"),
    DATA("DATA","数据"),
    HEART("HEART","心跳");

    private String code;
    private String desc;
    DataType(String code,String desc){
        this.code = code;
        this.desc = desc;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * 通过字符串code获取对应的枚举类型值
     * @param code
     * @return
     */
    public static DataType getDataType(String code){
        if (code == null) {
            return null;
        }
        for (DataType mathEnum : DataType.values()) {
            if (code.equals(mathEnum.getCode())){
                return mathEnum;
            }
        }
        return null;
    }
}
