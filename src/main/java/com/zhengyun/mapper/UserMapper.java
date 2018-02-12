package com.zhengyun.mapper;

import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {

    void addUser(String name);

    void addUserRecord(String name);
}
