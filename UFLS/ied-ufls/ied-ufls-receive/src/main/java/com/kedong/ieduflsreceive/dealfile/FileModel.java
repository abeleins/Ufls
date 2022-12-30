package com.kedong.ieduflsreceive.dealfile;

import java.util.Vector;


/**
 * 文件数据模型
 * <头信息，域名，列名，数据（多条），尾信息>
 */

public class FileModel {
    // 头信息
    private String head;
    // 结束信息
    private String tail;
    // 开始符
    private String startMark;
    // 结束符
    private String endMark;
    // 分割符
    private String mark;
    // 域名
    private Vector<String> attNames;
    // 列名
    private Vector<String> columnName;
    // 数据
    private Vector<Vector<String>> data;


    /**
     * 数据模型<br>
     * 分隔符不能为空
     *
     * @param startMark 开始符
     * @param endMark   结束符
     * @param mark      分割符
     */
    public FileModel(String startMark, String endMark, String mark) {
        this.startMark = startMark;
        this.endMark = endMark;
        this.mark = mark;
        init();
    }

    /**
     * 数据模型<br>
     * 分隔符不能为空<br>
     * 特殊符号注意前面加'/'
     *
     * @param mark 分割符
     *             服务器路径home/d5000/jibei/下
     */
    public FileModel(String mark) {
        this(null, null, mark);
    }

    private void init() {
        data = new Vector<>();

        setMark();
    }

    // 缺省一个格式化数据
//	public void format(){}

    /**
     * 多个分隔符连续出现时当一个处理
     */
    public void setMark() {
        this.mark = "['" + mark + "']+";
    }

    /**
     * 头信息
     */
    public String getHead() {
        return head;
    }

    /**
     * 头信息
     */
    public void setHead(String head) {
        this.head = head;
    }

    /**
     * 结束信息
     */
    public String getTail() {
        return tail;
    }

    /**
     * 结束信息
     */
    public void setTail(String tail) {
        this.tail = tail;
    }

    /**
     * 开始符号
     */
    public String getStartMark() {
        return startMark;
    }
//	/**
//	 * 开始符
//	 * */
//	public void setStartMark(String startMark) {
//		this.startMark = startMark;
//	}

    /**
     * 结束符
     */
    public String getEndMark() {
        return endMark;
    }
//	/**
//	 * 结束符
//	 * */
//	public void setEndMark(String endMark) {
//		this.endMark = endMark;
//	}

    /**
     * 数据分割符
     */
    public String getMark() {
        return mark;
    }
//	/**
//	 * 数据分隔符
//	 * */
//	public void setMark(String mark) {
//		this.mark = mark;
//	}

    /**
     * 表展示列名<br>
     * 列名不存在或者为null则返回域名
     */
    public Vector<String> getShowColnames() {
        if (columnName == null || columnName.size() == 0) {
            return attNames;
        }
        return columnName;
    }

    /**
     * 列名
     */
    public Vector<String> getColumnName() {
        return columnName;
    }

    /**
     * 列名
     */
    public void setColumnName(Vector<String> columnName) {
        this.columnName = columnName;
    }

    /**
     * 数据
     */
    public Vector<Vector<String>> getData() {
        return data;
    }

    /**
     * 数据
     */
    public void setData(Vector<Vector<String>> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "FileModel{" +
                "data=" + data +
                '}';
    }

    /**
     * 域名
     */
    public Vector<String> getAttNames() {
        return attNames;
    }

    /**
     * 域名
     */
    public void setAttNames(Vector<String> attNames) {
        this.attNames = attNames;
    }



}
