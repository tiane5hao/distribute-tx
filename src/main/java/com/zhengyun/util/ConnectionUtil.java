package com.zhengyun.util;

import com.zhengyun.tx.DistributeTxFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;

public class ConnectionUtil {

    private static DataSource dataSource;

    public static Connection getConnection(){
        initDataSource();
        return DataSourceUtils.getConnection(dataSource);
    }

    private static  void initDataSource() {
        if(dataSource == null){
            synchronized (ConnectionUtil.class){
                if(dataSource == null){
                    dataSource = (DataSource) SpringContextHolder.getBean("dataSource");
                }
            }

        }
    }

    public static void releaseConnection(Connection conn){
        DataSourceUtils.releaseConnection(conn, dataSource);
    }
}
