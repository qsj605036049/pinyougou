package com.pinyougou.pay.service;

import com.pinyougou.pojo.TbPayLog;

import java.util.Map;

/**
 * @author qinshiji
 * @data 2019/2/19 16:23
 */
public interface WeiXinService {
    /**
     * 创建二维码
     * @param out_trade_no
     * @param total_fee
     * @return
     */
    Map<String, Object> createNative(String out_trade_no, String total_fee) throws Exception;

    /**
     * 从redis得到paylog
     * @param userId
     * @return
     */
    TbPayLog getPayLogFromRedis(String userId);

    /**
     * 查询支付状态
     * @return
     * @param out_trade_no
     */
    Map<String, String> queryStatus(String out_trade_no) throws Exception;

    /**
     * 更新支付状态
     * @param out_trade_no
     * @param transaction_id
     */
    void updatePayStatus(String out_trade_no, String transaction_id);
}
