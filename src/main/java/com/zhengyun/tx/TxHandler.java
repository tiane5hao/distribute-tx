package com.zhengyun.tx;

import com.zhengyun.util.GlobalTxStatus;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.sql.Connection;
import java.sql.SQLException;

public class TxHandler {

    private boolean isNessary;

    private ProceedingJoinPoint pjp;

    private  Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public ProceedingJoinPoint getPjp() {
        return pjp;
    }

    public void setPjp(ProceedingJoinPoint pjp) {
        this.pjp = pjp;
    }

    public void setNessary(boolean nessary) {
        isNessary = nessary;
        if(isNessary){
            TransactionSynchronizationManager.setActualTransactionActive(true);
            TransactionSynchronizationManager.initSynchronization();
        }
    }

    public void openTxIfNessary()throws SQLException {
        if(!isNessary){
            return;
        }
        connection.setAutoCommit(false);
        System.out.println("开启事务");
        RegisterTxInfo txInfo = RegisterManager.createRegisterTxInfo();
        txInfo.setStatus(RegisterTxInfo.STATUS_INIT);
        ZKRegister.registerData(txInfo);
    }

    private void commitTxIfNessary() {
        if(!isNessary){
            return;
        }
        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DistributeTxFactory.releaseConnection(connection);
        }
        System.out.println("提交事务");
    }

    private void rollbackIfNessary()  {
        if(!isNessary){
            return;
        }
        try {
            connection.rollback();
            System.out.println("回滚事务");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DistributeTxFactory.releaseConnection(connection);
        }
    }

    public void updateTxStatus(int status){
        if(!isNessary){
            return;
        }
        RegisterTxInfo txInfo = RegisterManager.createRegisterTxInfo();
        txInfo.setStatus(status);
        ZKRegister.updateTxStatus(txInfo);
    }

    public void finishTx(){

        if(!isNessary){
            return;
        }
        final RegisterTxInfo txInfo = RegisterManager.createRegisterTxInfo();
        new Thread(new Runnable() {
            public void run() {

                long startTime = System.currentTimeMillis();
                long curTime = System.currentTimeMillis();
                boolean flag = false;
                while (curTime - startTime <30000){
                    GlobalTxStatus txStatus =  ZKRegister.queryGlobalTxStatus(txInfo);
                    switch (txStatus){
                        case success:
                            commitTxIfNessary();
                            flag = true;
                            break;
                        case failure:
                            rollbackIfNessary();
                            flag = true;
                            break;
                        case notFinish:
                            break;
                    }
                    if(flag){
                        //完成，退出循环
                        break;
                    }

                    try {
                        //每200毫秒查询一次
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    curTime = System.currentTimeMillis();
                }
            }
        }).start();


    }

    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        Thread.sleep(2000);
        long curTime = System.currentTimeMillis();

        System.out.println(curTime - startTime);
    }
}
