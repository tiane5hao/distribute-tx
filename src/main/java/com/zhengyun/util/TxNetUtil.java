package com.zhengyun.util;

import com.zhengyun.tx.DistributeTxFactory;
import com.zhengyun.tx.RegisterManager;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

import java.io.IOException;

public class TxNetUtil {

    public static Logger log = org.slf4j.LoggerFactory.getLogger(TxNetUtil.class);

    private static final HttpConnectionManager httpConnectionManager;

    public static final int CONN_TIMEOUT = 	30000;
    public static final int SOCK_TIMEOUT = 	30000;
    public static final int MAX_CONN = 50;
    public static final int MAX_CONN_PRE_HOST = 20;
    static {
        httpConnectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = httpConnectionManager.getParams();
        params.setConnectionTimeout(CONN_TIMEOUT);
        params.setSoTimeout(SOCK_TIMEOUT);
        params.setDefaultMaxConnectionsPerHost(MAX_CONN_PRE_HOST);
        params.setMaxTotalConnections(MAX_CONN);
    }

    public static <T>T post(Object param, String url, Class<T> clazz){
        T result = null;
        PostMethod postMethod = null;
        try {
            String resultJsonString = "";
            String  params = JsonUtil.objectToString(param);
            log.info("请求url： "+url);
            log.debug("输入报文："+params);
            // 发送报文
            HttpClient httpClient = new HttpClient(httpConnectionManager);
            postMethod = new PostMethod(url);
            postMethod.addRequestHeader("Content-Type","application/json;charset="+"UTF-8");
            postMethod.addRequestHeader("txId", RegisterManager.createTxId());
            RequestEntity requestEntity = new StringRequestEntity(params,"text/xml","UTF-8");
            postMethod.setRequestEntity(requestEntity);
            httpClient.executeMethod(postMethod);
            resultJsonString = postMethod.getResponseBodyAsString();
            return JsonUtil.stringToObject(resultJsonString, clazz);
        } catch (HttpException e) {
            log.error("客户端异常:" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            log.error("客户端异常:" + e.getMessage());
            e.printStackTrace();
        } finally {
            postMethod.releaseConnection();
        }
        return null;
    }
}
