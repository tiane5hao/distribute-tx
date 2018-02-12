package com.zhengyun.tx;

import com.zhengyun.util.*;
import org.I0Itec.zkclient.ZkClient;

import java.util.ArrayList;
import java.util.List;

public class ZKRegister {

    /**
     * zk中注册实务
     * @param detailInfo
     */
    public static void registerData(RegisterTxInfo detailInfo){
        ZkClient zkClient = getZKClient();
        String path = getPath(detailInfo);
        if(!zkClient.exists(path)){
            List<RegisterTxInfo> list = new ArrayList<RegisterTxInfo>(1);
            list.add(detailInfo);
            zkClient.createPersistent(path, JsonUtil.objectToString(list));
        }else {
            String data = zkClient.readData(path);
            List<RegisterTxInfo> list = JsonUtil.stringToList(data, RegisterTxInfo.class);
            list.add(detailInfo);
            zkClient.writeData(path, JsonUtil.objectToString(list));
        }
        getConnectPool().releaseConnection(zkClient);
    }

    private static String getPath(RegisterTxInfo detailInfo){
        return "/" + detailInfo.getTxId();
    }

    private static ZkClient getZKClient(){
        ZkClient zkClient = getConnectPool().getConnection();
        System.out.println("zkClient 连接对象" + zkClient);
        return zkClient;
    }

    private static ZKConnectionPool getConnectPool(){
        return SpringContextHolder.getBean(ZKConnectionPool.class);
    }

    /**
     * 更新实务状态
     * @param detailInfo
     */
    public static void updateTxStatus(RegisterTxInfo detailInfo){
        ZkClient zkClient = getZKClient();
        String data = zkClient.readData( getPath(detailInfo));
        List<RegisterTxInfo> list = JsonUtil.stringToList(data, RegisterTxInfo.class);
        List<RegisterTxInfo> newList = new ArrayList<RegisterTxInfo>(list.size());
        String serverId = PropertyUtil.newInstance().getStringProperty("serverId");
        for(RegisterTxInfo detail : list){
            if(serverId.equals(detail.getServerId())){
                if(detail.getStatus() != RegisterTxInfo.STATUS_INIT){
                    throw new IllegalStateException("zk中事物不是初始化态，不能修改");
                }
                detail.setStatus(detailInfo.getStatus());
            }
            newList.add(detail);
        }
        zkClient.writeData(getPath(detailInfo), JsonUtil.objectToString(newList));
        getConnectPool().releaseConnection(zkClient);
    }

    /**
     * 查询事务是否完成完成
     * @return
     */
    public static GlobalTxStatus queryGlobalTxStatus(RegisterTxInfo detailInfo){
        int notFinish = 0;
        int failure = 0;
        ZkClient zkClient = getZKClient();
        String data = zkClient.readData( getPath(detailInfo));
        getConnectPool().releaseConnection(zkClient);
        System.out.println(data);
        List<RegisterTxInfo> list = JsonUtil.stringToList(data, RegisterTxInfo.class);

        for(RegisterTxInfo txDetailInfo : list){
            if(txDetailInfo.getStatus() == RegisterTxInfo.STATUS_INIT){
                notFinish++;
            }

            if(txDetailInfo.getStatus() == RegisterTxInfo.STATUS_FAIL){
                failure ++;
                break;
            }
        }
        if(failure > 0){
            return GlobalTxStatus.failure;
        }

        if(notFinish > 0){
            return GlobalTxStatus.notFinish;
        }
        return GlobalTxStatus.success;
    }
}
