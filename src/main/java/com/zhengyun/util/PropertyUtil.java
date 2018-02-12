package com.zhengyun.util;

import java.io.IOException;
import java.util.Properties;

public class PropertyUtil {

    private static PropertyUtil propertyUtil;

    private Properties prop;

    private PropertyUtil(){
        prop = new Properties();
        try {
            prop.load(this.getClass().getClassLoader().getResourceAsStream("tx.properties"));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    public static PropertyUtil newInstance(){
        if(propertyUtil == null){
            synchronized (PropertyUtil.class){
                if(propertyUtil == null){
                    propertyUtil = new PropertyUtil();
                }
            }
        }
        return propertyUtil;
    }

    public String getStringProperty(String key){
        return prop.getProperty(key);
    }

}
