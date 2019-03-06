package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;
import java.util.Map;

/**
 * @author qinshiji
 * @data 2019/1/15 15:45
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/login")
    public Map<String,String> getName(){
        Map<String,String> map = new HashMap<>();
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("loginName", name);
        return map;
    }
}
