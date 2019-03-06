package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SecKillService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author qinshiji
 * @data 2019/2/20 16:42
 */
@RestController
@RequestMapping("/secKill")
public class SecKillController {
    @Reference
    private SecKillService secKillService;

    /**
     * 查询秒杀商品列表
     * @return
     */
    @RequestMapping("/findList")
    public List<TbSeckillGoods> findList(){
        return secKillService.findList();
    }

    /**
     * 查询秒杀商品详情
     * @param secKillGoodId
     * @return
     */
    @RequestMapping("/findOne")
    public TbSeckillGoods findOne(Long secKillGoodId){
        return secKillService.findOne(secKillGoodId);
    }

    @RequestMapping("/saveOrder")
    public Result saveOrder(Long secKillGoodId){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(userId)){
            return new Result(false, "请登录");
        }
        try {
            secKillService.saveOrder(userId,secKillGoodId);
            return new Result(true, "恭喜");
        }catch (RuntimeException e){
            e.printStackTrace();
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "下单失败");
        }
    }
}
