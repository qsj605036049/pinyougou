package com.pinyougou.seckill.task;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author qinshiji
 * @data 2019/2/20 15:56
 */
@Component
public class SeckillTask {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 每隔10s将秒杀商品导入redis缓存
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void importSecKillGoods() {

        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        criteria.andStartTimeLessThanOrEqualTo(new Date());
        criteria.andEndTimeGreaterThanOrEqualTo(new Date());
        criteria.andStockCountGreaterThan(0);
        List<TbSeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);
        for (TbSeckillGoods seckillGood : seckillGoods) {
            redisTemplate.boundHashOps("secKillGoods").put(seckillGood.getId(), seckillGood);
            System.out.println(seckillGood.getId());
        }
        System.out.println("import secKillGoods OK");

    }
}
