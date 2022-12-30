package com.kedong.ieduflscommon.enums;

/**
 * 功能类型枚举
 * 2022年12月28日16:51:18
 * mr_qin
 */
public enum OpType {

    ROUND("ROUND","轮次数据"),
    UNIT("UNIT","装置数据"),
    ACTITEM("ACTITEM","动作项数据"),
    REPORT("REPORT","动作报告数据");

    private String code;
    private String desc;
    OpType(String code,String desc){
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
    public static OpType getDataType(String code){
        if (code == null) {
            return null;
        }
        for (OpType mathEnum : OpType.values()) {
            if (code.equals(mathEnum.getCode())){
                return mathEnum;
            }
        }
        return null;
    }
}
