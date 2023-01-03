package com.kedong.ieduflscommon.enums;

/**
 * @Author: abel
 * @Date: 2022/12/31 9:15
 * @regionNameription:地市局名称
 */
public enum CityEnum {
    /**
     * 山西11地市
     * 天津17区加天津市直调
     * 冀北5地市
     */

    SX_DT("DT","大同市"),
    SX_SZ("SZ","朔州市"),
    SX_XZ("XZ","忻州市"),
    SX_TY("TY","太原市"),
    SX_LL("LL","吕梁市"),
    SX_YQ("YQ","阳泉市"),
    SX_JZ("JZ","晋中市"),
    SX_CZ("CZ","长治市"),
    SX_LF("LF","临汾市"),
    SX_JC("JC","晋城市"),
    SX_YC("YC","运城市"),

    TJ_BH("BH","滨海新区"),
    TJ_HP("HP","和平区"),
    TJ_HD("HD","河东区"),
    TJ_HX("HX","河西区"),
    TJ_NK("NK","南开区"),
    TJ_HB("HB","河北区"),
    TJ_HQ("HQ","红桥区"),
    TJ_DL("DL","东丽区"),
    TJ_XQ("XQ","西青区"),
    TJ_JN("JN","津南区"),
    TJ_BC("BC","北辰区"),
    TJ_WQ("WQ","武清区"),
    TJ_BD("BD","宝坻区"),
    TJ_JH("JH","静海区"),
    TJ_NH("NH","宁河区"),
    TJ_JZ("JZ","蓟州区"),
    TJ_TJ("TJ","天津"),

    HB_ZJK("ZJK","张家口市"),
    HB_CD("CD","承德市"),
    HB_TS("TS","唐山市"),
    HB_QHD("QHD","秦皇岛市"),
    HB_LF("LF","廊坊市");


    private String code;
    private String regionName;
    CityEnum(String code, String regionName){
        this.code = code;
        this.regionName = regionName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    /**
     * 通过字符串code获取对应的枚举类型值
     * @param code
     * @return
     */
    public static CityEnum getCity(String code){
        if (code == null) {
            return null;
        }
        for (CityEnum mathEnum : CityEnum.values()) {
            if (code.equals(mathEnum.getCode())){
                return mathEnum;
            }
        }
        return null;
    }
}
