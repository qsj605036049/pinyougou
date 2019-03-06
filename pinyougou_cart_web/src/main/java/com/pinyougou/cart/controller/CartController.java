package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.cart.service.CartService;
import com.sun.org.apache.bcel.internal.generic.FADD;
import entity.Result;
import groupEntity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author qinshiji
 * @data 2019/2/16 16:51
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    /**
     * 查询购物车列表
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);
        //在cookie中查询sessionId,在redis中得到购物车列表
        String sessionId = getSessionId();
        List<Cart> cartList_sessionId = cartService.getCartListFromRedis(sessionId);
        if ("anonymousUser".equals(username)){
            //未登录
            System.out.println("查询redis_sessionId");
            return cartList_sessionId;
        }else {
            //已登录
            List<Cart> cartList_username = cartService.getCartListFromRedis(username);
            //如果临时购物车存在数据则合并
            if (cartList_sessionId.size()>0){
              cartList_username  = cartService.mergeCartList(cartList_sessionId,cartList_username);
              //合并后清除
              cartService.deleteCartList(sessionId);
              //合并后保存至redis
              cartService.saveCartListToRedisByUsername(cartList_username,username);
            }
            System.out.println("查询redis_username");
            return cartList_username;
        }
    }

    /**
     * 获取sessionId
     * @return
     */
    private String getSessionId() {
        String sessionId = CookieUtil.getCookieValue(request, "sessionId");
        if (sessionId==null){
            sessionId = request.getSession().getId();
            CookieUtil.setCookie(request, response, "sessionId", sessionId, 60*60*24*7, "utf-8");
        }
        return sessionId;
    }

    /**
     * 添加商品至购物车
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addItemToCartList")
    @CrossOrigin(origins = "http://item.pinyougou.com",allowCredentials = "true")
    public Result addItemToCartList(Long itemId,Integer num){
        System.out.println(itemId);
        System.out.println(num);
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            List<Cart> cartList = findCartList();
            //添加商品到集合
            cartList = cartService.addItemToCartList(cartList, itemId, num);
            if ("anonymousUser".equals(username)){
                //未登录,保存sessionId到redis
                System.out.println("保存到redis_sessionId");
                String sessionId = getSessionId();
                cartService.saveCartListToRedisBySessionId(cartList,sessionId);
            }else {
                //已登录,保存username到redis
                System.out.println("保存到redis_username");
                cartService.saveCartListToRedisByUsername(cartList, username);
            }
            return new Result(true, "添加购物车成功");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, "添加购物车失败");
        }
    }
}
