package com.pinyougou.search.service;

import java.util.Map;

/**
 * @author qinshiji
 * @data 2019/1/24 16:19
 */
public interface SearchService {

    /**
     * 搜索sku
     * @param searchMap
     * @return
     */
    Map<String, Object> searchItem(Map searchMap);


}
