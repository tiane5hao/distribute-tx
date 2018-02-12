package com.zhengyun.tx;

import com.zhengyun.tx.annotation.Business;
import com.zhengyun.util.ConnectionUtil;
import com.zhengyun.util.PropertyUtil;
import com.zhengyun.util.SpringContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;

public class DistributeTxFactory {



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
                //设置数据库连接
                txHandler.setConnection(ConnectionUtil.getConnection());
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return txHandler;
    }




}
