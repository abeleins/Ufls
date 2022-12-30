package com.kedong.ieduflscommon.util;

import com.kedong.ieduflscommon.enums.DataType;

import java.io.File;
import java.util.Base64;
import java.util.Date;

/**
 * 工具方法
 * 2021年2月20日14:52:23
 * qxh
 */
public class DealFileTool {

    /*
     * 获取文件扩展名
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /*
     * 获取不带扩展名的文件名
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }


    /**
     * base64编码
     *
     * @param value
     * @return
     * @throws Exception
     */
    public static String getBase64Encode(byte[] value) {
        String encodedText = "";
        try {

            Base64.Encoder encoder = Base64.getEncoder();
            //编码
            encodedText = encoder.encodeToString(value);
//            System.out.println(encodedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodedText;
    }

    /**
     * base64解码
     *
     * @param value
     * @return
     */
    public static byte[] getBase64Decode(String value) {
        byte[] encodedText = null;
        if(value == null || value.length() <=0){
            value = "";
        }
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            encodedText = decoder.decode(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodedText;
    }

    /**
     * 获取录波文件相对于FILE目录的相对路径
     *
     * @param absolutePath
     * @return
     */
    public static String getRecordRelativePathName(String absolutePath) {
        return absolutePath.substring(absolutePath.lastIndexOf("RESPONSE") + 9, absolutePath.lastIndexOf("."));
    }

    /**
     * 获取文件存储路径
     * @param dataType MODEL：模型/DATA：数据
     * @return
     */
    public static String getPath (String dataType){
        String path = "";
        switch (DataType.getDataType(dataType)){
            case MODEL://模型
                path = dataType;
                break;
            case DATA://数据
                path = path + File.separator + TimeTool.getTimeStr(new Date().getTime(),TimeTool.FORMIT_MONTH);
                break;
            case HEART://心跳
                path = "heart";
                break;
            default:
                throw new IllegalArgumentException("路径解析出错，请检查DataType数据类型是否正确");
        }
        return path;
    }
}
