package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.pay.service.WeiXinService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import utils.HttpClient;
import utils.IdWorker;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qinshiji
 * @data 2019/2/19 16:26
 */
@Service
@Transactional
public class WeiXinServiceImpl implements WeiXinService {

    @Value("${partnerkey}")
    private String partnerkey;
    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${notifyurl}")
    private String notifyurl;
    @Autowired
    private IdWorker idWorker;
    @Override
    public Map<String, Object> createNative(String out_trade_no, String total_fee) throws Exception {
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
        httpClient.setHttps(true);
        Map<String, String> data =  new HashMap<>();
        data.put("appid", appid);
        data.put("mch_id", partner);
        data.put("nonce_str", WXPayUtil.generateNonceStr());
        data.put("body", "品优购");
        data.put("out_trade_no", out_trade_no);
        data.put("total_fee", total_fee);
        data.put("spbill_create_ip", "127.0.0.1");
        data.put("notify_url", notifyurl);
        data.put("trade_type", "NATIVE");
        String xmlParam  = WXPayUtil.generateSignedXml(data,partnerkey);
        httpClient.setXmlParam(xmlParam);
        httpClient.post();
        String content = httpClient.getContent();
        Map<String, String> map = WXPayUtil.xmlToMap(content);
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("out_trade_no", out_trade_no);
        resultMap.put("total_fee", total_fee);
        resultMap.put("code_url", map.get("code_url"));
        return resultMap;
    }
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public TbPayLog getPayLogFromRedis(String userId) {
        TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
        if (payLog==null){
            throw new RuntimeException("错误");
        }
        return payLog;
    }

    @Override
    public Map<String, String> queryStatus(String out_trade_no) throws Exception {
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
        httpClient.setHttps(true);
        Map<String, String> data =  new HashMap<>();
        data.put("appid", appid);
        data.put("mch_id", partner);
        data.put("nonce_str", WXPayUtil.generateNonceStr());
        data.put("out_trade_no", out_trade_no);
        String xmlParam  = WXPayUtil.generateSignedXml(data,partnerkey);
        httpClient.setXmlParam(xmlParam);
        httpClient.post();
        String content = httpClient.getContent();
        Map<String, String> map = WXPayUtil.xmlToMap(content);
        return map;
    }
    @Autowired
    private TbPayLogMapper payLogMapper;

    @Autowired
    private TbOrderMapper orderMapper;

    @Override
    public void updatePayStatus(String out_trade_no, String transaction_id) {
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        payLog.setPayTime(new Date());
        payLog.setTransactionId(transaction_id);
        payLog.setTradeState("1");
        String orderList = payLog.getOrderList();
        String[] split = orderList.split(",");
        for (String s : split) {
            TbOrder order = orderMapper.selectByPrimaryKey(Long.valueOf(s));
            order.setPaymentTime(new Date());
            order.setStatus("2");
            orderMapper.updateByPrimaryKey(order);
        }
        payLogMapper.updateByPrimaryKey(payLog);

        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
    }
}
