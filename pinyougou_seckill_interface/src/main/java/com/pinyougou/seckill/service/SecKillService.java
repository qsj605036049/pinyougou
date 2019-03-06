package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillGoods;

import java.util.List;

/**
 * @author qinshiji
 * @data 2019/2/20 16:37
 */
public interface SecKillService {
    /**
     * 查询所有秒杀商品列表
     * @return
     */
    List<TbSeckillGoods> findList();

    /**
     * 查询秒杀商品
     * @param secKillGoodId
     * @return
     */
    TbSeckillGoods findOne(Long secKillGoodId);

    /**
     * 保存订单
     * @param userId
     * @param secKillGoodId
     */
    void saveOrder(String userId, Long secKillGoodId);
}
