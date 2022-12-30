package com.kedong.ieduflsweb.datasourceconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceHolder {
    private static final Logger log = LoggerFactory.getLogger(DataSourceHolder.class);

    public static final String WRITE_DATASOURCE = "write";
    public static final String READ_DATASOURCE = "read";
    private static final ThreadLocal<String> local = new ThreadLocal<>();

    public static void putDataSource(String dataSource) {
        local.set(dataSource);
    }

    public static String getDataSource() {
        return local.get();
    }

    public static void clearDataSource() {
        local.remove();
    }
}
