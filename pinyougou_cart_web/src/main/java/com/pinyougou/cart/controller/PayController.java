package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeiXinService;
import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.omg.CORBA.MARSHAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qinshiji
 * @data 2019/2/19 16:44
 */
@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private WeiXinService weiXinService;

    /**
     * 创建二维码
     * @return
     */
    @RequestMapping("/createNative")
    public Map<String,Object> createNative(){
        try {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            TbPayLog payLog = weiXinService.getPayLogFromRedis(userId);
            return weiXinService.createNative(payLog.getOutTradeNo(),payLog.getTotalFee()+"");
        } catch (RuntimeException e) {
            e.printStackTrace();
            Map<String, Object> map = new HashMap<>();
            map.put("message", e.getMessage());
            return map;
        }catch (Exception e){
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * 查询支付状态
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryStatus")
    public Result queryStatus(String out_trade_no){
        try {
            int count = 0;
            while (true){
                Map<String,String> map =  weiXinService.queryStatus(out_trade_no);
                String trade_state = map.get("trade_state");
                if ("SUCCESS".equals(trade_state)){
                    String transaction_id = map.get("transaction_id");
                    weiXinService.updatePayStatus(out_trade_no,transaction_id);
                    return new Result(true, "支付成功");
                }
                Thread.sleep(3000);
                count++;
                if (count>100){
                    createNative();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "支付失败");
        }
    }
}
