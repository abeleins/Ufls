package com.kedong.ieduflsreceive.datasourceconfig;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static com.kedong.ieduflsreceive.datasourceconfig.DataSourceHolder.READ_DATASOURCE;
import static com.kedong.ieduflsreceive.datasourceconfig.DataSourceHolder.WRITE_DATASOURCE;


@Component
public class CustomRoutingDataSource extends AbstractRoutingDataSource {
    private static final Logger log = LoggerFactory.getLogger(CustomRoutingDataSource.class);

    @Resource(name = "writeDataSourceProperties")
    private DataSourceProperties writeProperties;
    @Resource(name = "readDataSourceProperties")
    private DataSourceProperties readProperties;
    @Override
    public void afterPropertiesSet() {
        DataSource writeDataSource =
                writeProperties.initializeDataSourceBuilder().type(DruidDataSource.class).build();
        DataSource readDataSource =
                readProperties.initializeDataSourceBuilder().type(DruidDataSource.class).build();
        setDefaultTargetDataSource(writeDataSource);
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(WRITE_DATASOURCE, writeDataSource);
        dataSourceMap.put(READ_DATASOURCE, readDataSource);
        setTargetDataSources(dataSourceMap);
        super.afterPropertiesSet();
    }
    @Override
    protected Object determineCurrentLookupKey() {
        String key = DataSourceHolder.getDataSource();
        if (key == null) {
            log.info("使用数据源：写库数据源");
            // default datasource
            return WRITE_DATASOURCE;
        }
        log.info("使用数据源：读库数据源");
        return key;
    }
}
