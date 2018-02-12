package com.zhengyun.service.impl;

import com.zhengyun.mapper.UserMapper;
import com.zhengyun.service.TestService;
import com.zhengyun.tx.annotation.Business;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService{

    @Autowired
    UserMapper userMapper;

    @Business
    public void addUser(String name){

        userMapper.addUser(name);
        /*if(true){
            throw new IllegalArgumentException();
        }*/
        userMapper.addUserRecord(name);
    }
}
