package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.SearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author qinshiji
 * @data 2019/1/24 16:21
 */
@RestController
@RequestMapping("/search")
public class SearchController {
    @Reference
    private SearchService searchService;

    @RequestMapping("/searchItem")
    public Map<String,Object> searchItem(@RequestBody Map searchMap){
        return searchService.searchItem(searchMap);
    }
}
