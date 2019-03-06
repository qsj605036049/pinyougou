package com.pinyougou.sms.controller;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.pinyougou.sms.utils.SmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qinshiji
 * @data 2019/2/13 16:09
 */
@RestController
@RequestMapping("/sms")
public class SmsDemoController {
    @Autowired
    private SmsUtil smsUtil;

    @RequestMapping(value = "/sendMessage",method = RequestMethod.POST)
    public Map<String,String> sendMessage(String phoneNumbers, String signName, String templateCode, String param){
        try {
            SendSmsResponse response = smsUtil.sendSms(phoneNumbers, signName, templateCode, param);
            System.out.println("Code=" + response.getCode());
            System.out.println("Message=" + response.getMessage());
            System.out.println("RequestId=" + response.getRequestId());
            System.out.println("BizId=" + response.getBizId());
            return new HashMap<>();
        } catch (ClientException e) {
            e.printStackTrace();
            System.out.println("错误");
            return new HashMap<>();
        }
    }
}
