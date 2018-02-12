package com.zhengyun.tx;

import org.apache.commons.lang.StringUtils;

import java.util.UUID;

public class RegisterTxInfo {



    public static final Integer STATUS_INIT = 0;

    public static final Integer STATUS_SUCCESS = 1;

    public static final Integer STATUS_FAIL = 2;

    private String txId;

    private String serverId;

    private Integer status;

    public RegisterTxInfo(){

    }

    public RegisterTxInfo(String txId, String serverId) {
        this.txId = txId;
        this.serverId = serverId;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


}
