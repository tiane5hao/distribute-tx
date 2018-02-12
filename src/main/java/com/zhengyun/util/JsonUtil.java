package com.zhengyun.util;

import com.zhengyun.tx.RegisterTxInfo;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.util.List;

public class JsonUtil {

    private static Logger logger = Logger.getLogger(JsonUtil.class);

    public static String objectToString(Object obj){
        ObjectMapper mapper = getDefaultMapper();
        try {
            return mapper.writeValueAsString(obj);
        }catch (Exception e){
            logger.error("JsonUtil objectToString error", e);
        }
        return null;
    }

    public static void main(String[] args) {
        String data = "[{\"txId\":\"aa2ab737-4b19-44f2-9257-268379d1466c\",\"serverId\":\"10000\",\"status\":0}]";
        List<RegisterTxInfo> list = JsonUtil.stringToList(data, RegisterTxInfo.class);
        System.out.println(list);
    }

    public static <T>T stringToObject(String str, Class<T> clazz){
        ObjectMapper mapper = getDefaultMapper();
        try {
            return mapper.readValue(str, clazz);
        }catch (Exception e){
            logger.error("JsonUtil objectToString error", e);
        }
        return null;
    }

    public static <T>List<T> stringToList(String str, Class<T> clazz){
        ObjectMapper mapper = getDefaultMapper();
        try {
            JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, new Class[] { clazz });
            return mapper.readValue(str, javaType);
        }catch (Exception e){
            logger.error("JsonUtil objectToString error", e);
        }
        return null;
    }

    public static ObjectMapper getDefaultMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.getDeserializationConfig().set(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}
