package com.zhengyun.tx;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 事务代理类
 */
@Aspect
@Component
public class MyTxProxy {


    @Around("execution(* com.zhengyun.service.impl.*.*(..))")
    public Object aroundAdvice(ProceedingJoinPoint jp)  {
        Object result = null;
        //创建事务处理类
        TxHandler txHandler = DistributeTxFactory.createTxHandler(jp);
        try {
            //开启事物
            txHandler.openTxIfNessary();
            Object[] args = jp.getArgs() ;
            result = jp.proceed(args);
            //更新事务成功
            txHandler.updateTxStatus(RegisterTxInfo.STATUS_SUCCESS);
        }catch (Throwable e){
            e.printStackTrace();
            //更新事务失败
            txHandler.updateTxStatus(RegisterTxInfo.STATUS_FAIL);

        }
        //提交事务或者回滚事务
        txHandler.finishTx();

        return result;
    }
}
