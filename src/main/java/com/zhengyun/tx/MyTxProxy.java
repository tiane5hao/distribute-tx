package com.zhengyun.tx;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Aspect
@Component
public class MyTxProxy {


    @Around("execution(* com.zhengyun.service.impl.*.*(..))")
    public Object aroundAdvice(ProceedingJoinPoint jp)  {
        Object result = null;
        TxHandler txHandler = DistributeTxFactory.createTxHandler(jp);
        try {
            txHandler.openTxIfNessary();
            Object[] args = jp.getArgs() ;
            result = jp.proceed(args);
            txHandler.updateTxStatus(RegisterTxInfo.STATUS_SUCCESS);
        }catch (Throwable e){
            e.printStackTrace();
            txHandler.updateTxStatus(RegisterTxInfo.STATUS_FAIL);

        }
        txHandler.finishTx();

        return result;
    }
}
