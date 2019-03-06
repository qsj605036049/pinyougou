package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import groupEntity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author qinshiji
 * @data 2019/2/16 16:05
 */
@Service
public class CartServiceImpl implements CartService {
    /**
     * 先根据商品id查询该商品关联的商家，是否存在于购物车列表中
     1该商家对应购物车对象不存在与购物车列表
     创建购物车对象，再存入购物车列表中
     创建购物车对象时，需要指定该购物车商家信息，以及构建购物车明细列表和购物车明细对象，
     将购物车明细对象添加到购物车明细列表中，将购物车明细列表添加到购物车对象，将购物车对象
     添加到购物车列表中

     2该商家对应购物车对象存在与购物车列表
     判断该商品是否存在于购物车商品明细列表中
     1、如果该商品不存在于购物车明细列表
     创建购物车明细对象，再添加到购物车明细列表中

     2、如果该商品存在于购物车明细列表
     修改购物车明细对象的商品数据和小计金额
     */
    @Autowired
    private TbItemMapper itemMapper;
    @Override
    public List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, int num) {
        //根据ItemId查询sellerId,判断是否存在于购物车列表
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if(item==null){
            //添加商品时，商家将商品删除
            throw new RuntimeException("很抱歉，商品已经删除");
        }

        if(!item.getStatus().equals("1")){
            throw new RuntimeException("很抱歉，商品无效");
        }

        String sellerId = item.getSellerId();

        Cart cart = getCart(cartList, sellerId);
        if (cart == null){
            //该商家对应购物车对象不存在与购物车列表
            //创建购物车对象并填写购物车明细
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            List<TbOrderItem> orderItemList = new ArrayList<>();
            TbOrderItem orderItem = createOrderItem(itemId, num, item);
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            cartList.add(cart);

        }else {
            //该商家对应购物车对象存在与购物车列表

            //判断该商品是否存在于购物车对象中
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            TbOrderItem orderItem = getOrderItem(itemId, orderItemList);
            if (orderItem == null){
                //如果购物车对象中没有该商品,创建商品对象添加到购物车对象
                TbOrderItem orderItem1 = createOrderItem(itemId, num, item);
                orderItemList.add(orderItem1);
            }else {
                //购物车中存在该商品
                //修改商品数据 和 金额统计
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));

                //当num为0时删除
                if (orderItem.getNum()<=0){
                    orderItemList.remove(orderItem);
                }
                //当orderItemList为空时,删除购物车对象
                if (orderItemList.size()==0){
                    cartList.remove(cart);
                }

            }


        }
        return cartList;
    }

    /**
     * 在redis中获取CartList
     * @param sessionId
     * @return
     */
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<Cart> getCartListFromRedis(String key) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundValueOps(key).get();
        if (cartList==null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    /**
     * 合并购物车
     * @param cartList_sessionId
     * @param cartList_username
     * @return
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList_sessionId, List<Cart> cartList_username) {
        for (Cart cart : cartList_sessionId) {
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem orderItem : orderItemList) {
                cartList_username = addItemToCartList(cartList_username, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return cartList_username;
    }

    /**
     * 在redis中删除购物车
     * @param sessionId
     */
    @Override
    public void deleteCartList(String sessionId) {
        redisTemplate.delete(sessionId);
    }

    /**
     * 根据username保存至redis
     * @param cartList_username
     * @param username
     */
    @Override
    public void saveCartListToRedisByUsername(List<Cart> cartList_username, String username) {
        redisTemplate.boundValueOps(username).set(cartList_username);
    }

    /**
     * 未登录保存购物车至redis
     * @param cartList
     * @param sessionId
     */
    @Override
    public void saveCartListToRedisBySessionId(List<Cart> cartList, String sessionId) {
        redisTemplate.boundValueOps(sessionId).set(cartList, 7L , TimeUnit.DAYS);
    }

    /**
     * 判断商品是否存在于购物车对象中
     * @param itemId
     * @param orderItemList
     * @return
     */
    private TbOrderItem getOrderItem(Long itemId, List<TbOrderItem> orderItemList) {
        for (TbOrderItem orderItem : orderItemList) {
            if (itemId.equals(orderItem.getItemId())){
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 创建orderItem对象
     * @param itemId
     * @param num
     * @param item
     */
    private TbOrderItem createOrderItem(Long itemId, int num, TbItem item) {
        TbOrderItem orderItem = new TbOrderItem();
        if (num<=0){
            throw new RuntimeException("非法操作,至少添加一件商品");
        }
        orderItem.setItemId(itemId);
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
        return orderItem;
    }

    /**
     * 根据sellerId查询购物车列表中购物车对象
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart getCart(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (sellerId.equals(cart.getSellerId())){
                return cart;
            }
        }
        return null;
    }
}
