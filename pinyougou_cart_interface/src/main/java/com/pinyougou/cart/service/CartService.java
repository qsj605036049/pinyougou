package com.pinyougou.cart.service;

import groupEntity.Cart;

import java.util.List;

/**
 * @author qinshiji
 * @data 2019/2/16 15:55
 */
public interface CartService {

    /**
     * 添加商品至购物车列表
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    List<Cart> addItemToCartList(List<Cart> cartList,Long itemId,int num);

    /**
     * 在redis中获取cartList
     * @param sessionId
     * @return
     */
    List<Cart> getCartListFromRedis(String sessionId);

    /**
     * 合并购物车
     * @param cartList_sessionId
     * @param cartList_username
     * @return
     */
    List<Cart> mergeCartList(List<Cart> cartList_sessionId, List<Cart> cartList_username);

    /**
     * redis中删除临时购物车
     * @param sessionId
     */
    void deleteCartList(String sessionId);

    /**
     * 登录后保存购物车至redis
     * @param cartList_username
     * @param username
     */
    void saveCartListToRedisByUsername(List<Cart> cartList_username, String username);

    /**
     * 未登录保存购物车至redis
     * @param cartList
     * @param sessionId
     */
    void saveCartListToRedisBySessionId(List<Cart> cartList, String sessionId);
}
