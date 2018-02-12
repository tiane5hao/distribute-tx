package com.zhengyun.tx;

import com.zhengyun.util.PropertyUtil;
import org.apache.commons.lang.StringUtils;

import java.util.UUID;

public class RegisterManager {

    //线程本地变量存放事务id
    private static ThreadLocal<String> threadLocal = new ThreadLocal<String>();

    public static String createTxId() {
        String txId = threadLocal.get();
        if(StringUtils.isNotBlank(txId)){
            return txId;
        }
        txId = UUID.randomUUID().toString();
        threadLocal.set(txId);
        return txId;
    }

    public static void setTxId(String txId){
        threadLocal.set(txId);
    }

    public static void removeTxId(){
        threadLocal.remove();
    }

    public static RegisterTxInfo createRegisterTxInfo() {
        String serverId = PropertyUtil.newInstance().getStringProperty("serverId");
        return new RegisterTxInfo( createTxId(), serverId);
    }
}
