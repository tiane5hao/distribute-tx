package com.zhengyun.controller;

import com.zhengyun.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class UserController {

    @Autowired
    TestService testService;

    @RequestMapping("/addUser")
    @ResponseBody
    public void addUser(@RequestBody Map<String, String> map){
         testService.addUser(map.get("name"));
    }
}
