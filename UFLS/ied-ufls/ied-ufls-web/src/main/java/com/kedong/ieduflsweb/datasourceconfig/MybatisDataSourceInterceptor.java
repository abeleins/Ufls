package com.kedong.ieduflsweb.datasourceconfig;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Properties;

@Component
@Intercepts({
        @Signature(type = Executor.class, method = "update",
                args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class,
                        CacheKey.class, BoundSql.class})})

public class MybatisDataSourceInterceptor implements Interceptor {
    private static final Logger log = LoggerFactory.getLogger(MybatisDataSourceInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        boolean synchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();
        if (!synchronizationActive) {
            log.info("未使用事务");
            Object[] objects = invocation.getArgs();
            MappedStatement ms = (MappedStatement) objects[0];
            log.info(ms.getSqlCommandType().toString());
            if (ms.getSqlCommandType().equals(SqlCommandType.SELECT)) {
                DataSourceHolder.putDataSource(DataSourceHolder.READ_DATASOURCE);
            }else {
                DataSourceHolder.putDataSource(DataSourceHolder.WRITE_DATASOURCE);
            }
        }
        //TODO 对执行的sql计时以及打印sql
        long startTime = System.currentTimeMillis();
        try {
            return invocation.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            long sqlCost = endTime - startTime;
            System.out.println("执行耗时 : [" + sqlCost + "ms ] ");
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
