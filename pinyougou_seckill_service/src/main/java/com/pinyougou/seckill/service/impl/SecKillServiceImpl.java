package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SecKillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import utils.IdWorker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author qinshiji
 * @data 2019/2/20 16:40
 */
@Service
@Transactional
public class SecKillServiceImpl implements SecKillService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询秒杀商品列表
     *
     * @return
     */
    @Override
    public List<TbSeckillGoods> findList() {
        List<TbSeckillGoods> secKillGoods = redisTemplate.boundHashOps("secKillGoods").values();
        if (secKillGoods == null) {
            secKillGoods = new ArrayList<>();
        }
        return secKillGoods;
    }

    @Override
    public TbSeckillGoods findOne(Long secKillGoodId) {
        TbSeckillGoods secKillGood = (TbSeckillGoods) redisTemplate.boundHashOps("secKillGoods").get(secKillGoodId);
        return secKillGood;
    }

    /**
     * 保存秒杀订单
     *
     * @param userId
     * @param secKillGoodId
     */
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;

    @Override
    public void saveOrder(String userId, Long secKillGoodId) {
        Boolean member = redisTemplate.boundSetOps("seckill_goods_" + userId).isMember(secKillGoodId);
        if (member){
            throw new RuntimeException("对不起,只能购买一件");
        }
        TbSeckillGoods secKillGood = (TbSeckillGoods) redisTemplate.boundHashOps("secKillGoods").get(secKillGoodId);
        if (secKillGood==null || secKillGood.getStockCount()<=0){
            throw  new RuntimeException("该商品已售罄");
        }
        redisTemplate.boundSetOps("seckill_goods_" + userId).add(secKillGoodId);
        TbSeckillOrder seckillOrder = new TbSeckillOrder();
        seckillOrder.setId(idWorker.nextId());
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setMoney(secKillGood.getPrice());
        seckillOrder.setSeckillId(secKillGoodId);
        seckillOrder.setUserId(userId);
        seckillOrder.setStatus("1");

        seckillOrderMapper.insert(seckillOrder);
        //更新库存
        Integer stockCount = secKillGood.getStockCount();
        secKillGood.setStockCount(stockCount - 1);
        if (secKillGood.getStockCount() <= 0) {
            //库存为0时删除
            redisTemplate.boundHashOps("secKillGoods").delete(secKillGoodId);
        } else {
            //库存不为0时更新
            redisTemplate.boundHashOps("secKillGoods").put(secKillGoodId, secKillGood);
        }
    }
}
