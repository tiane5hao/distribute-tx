package com.zhengyun.tx;

import com.zhengyun.tx.annotation.Business;
import com.zhengyun.util.PropertyUtil;
import com.zhengyun.util.SpringContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;

public class DistributeTxFactory {

    private static DataSource dataSource;

    public static TxHandler createTxHandler(ProceedingJoinPoint pjp){
        TxHandler txHandler = new TxHandler();
        try {
            String methodName=pjp.getSignature().getName();
            Class<?> classTarget=pjp.getTarget().getClass();
            Class<?>[] par=((MethodSignature) pjp.getSignature()).getParameterTypes();
            Method objMethod=classTarget.getMethod(methodName, par);

            Business business=objMethod.getAnnotation(Business.class);
            if(business == null){
                txHandler.setNessary(false);
            }else {
                txHandler.setNessary(true);
                txHandler.setPjp(pjp);
                txHandler.setConnection(getConnection());
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return txHandler;
    }

    private static Connection getConnection(){
        initDataSource();
        return DataSourceUtils.getConnection(dataSource);
    }

    private static  void initDataSource() {
        if(dataSource == null){
            synchronized (DistributeTxFactory.class){
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
