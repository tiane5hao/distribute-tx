package com.zhengyun.util;

import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ZKConnectionPool implements ApplicationListener<ContextRefreshedEvent> {

    private static String zkAddr = "172.16.0.145:2181";

    private GenericObjectPool pool;

    public ZKConnectionPool(){

    }

    public void initZKConnectionPool(Config config, String addr){
        ZKConnectionFactory factory = new ZKConnectionFactory(addr);
        pool = new GenericObjectPool(factory, config);
    }

    public ZkClient getConnection() {
        try {
            return (ZkClient)pool.borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void releaseConnection(ZkClient zkClient){
        if(zkClient == null){
            return;
        }
        try{
            pool.returnObject(zkClient);
        }catch(Exception e){
            if(zkClient != null){
                try{
                    zkClient.close();
                }catch(Exception ex){
                    //
                }
            }
        }
    }

    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        Config config = new Config();
        config.maxActive = 16;
        config.minIdle = 4;
        config.maxWait = 30000;

        initZKConnectionPool(config, zkAddr);
    }

    class ZKConnectionFactory extends BasePoolableObjectFactory {

        private String addr;

        public ZKConnectionFactory(String addr) {
            this.addr = addr;
        }

        public Object makeObject() throws Exception {
            ZkClient zkClient = new ZkClient(addr, 100000);
            return zkClient;
        }

        public void destroyObject(Object obj) throws Exception {
            if(obj instanceof ZkClient){
                ((ZkClient)obj).close();
            }
        }

        public boolean validateObject(Object obj) {
            return true;
        }
    }
}
