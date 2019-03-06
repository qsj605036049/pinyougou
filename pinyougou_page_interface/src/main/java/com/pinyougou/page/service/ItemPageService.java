package com.pinyougou.page.service;

import java.io.IOException;

/**
 * @author qinshiji
 * @data 2019/1/26 16:06
 */
public interface ItemPageService {
    /**
     * 生成商品详细页
     * @param goodsId
     */
    void genItemPage(Long goodsId) throws Exception;
}
