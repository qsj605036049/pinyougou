package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qinshiji
 * @data 2019/1/24 16:20
 */
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Map<String, Object> searchItem(Map searchMap) {
        Map<String, Object> resultMap = new HashMap<>();
        String category = (String) searchMap.get("category");
        Map<String, Object> search = search(searchMap);
        Map<String, Object> categoryList = searchCategory(searchMap);
        if (category!=null&&!"".equals(category)){
            Map<String, Object> brandListAndSpecList = getBrandListAndSpecList(category);
            resultMap.putAll(brandListAndSpecList);
        }else {
            Map<String, Object> brandListAndSpecList = getBrandListAndSpecList(((List<String>) categoryList.get("categoryList")).get(0));
            resultMap.putAll(brandListAndSpecList);
        }
        resultMap.putAll(search);
        resultMap.putAll(categoryList);

        return resultMap;
    }



    /**
     * 搜索sku
     * @param searchMap
     * @return
     */
    private Map<String, Object> search(Map searchMap) {
        Map<String,Object> map = new HashMap<>();


        HighlightQuery query = new SimpleHighlightQuery();

//       1.根据关键字查询
        Criteria criteria = new Criteria("item_keywords");
        String keywords = (String) searchMap.get("keywords");
        if (keywords!=null && !"".equals(keywords)){
            criteria = new Criteria("item_keywords");
            criteria.contains(keywords);
        }else{
            criteria = new Criteria();
            criteria.expression("*:*");
        }
        query.addCriteria(criteria);
        //配置过滤条件

//        2.根据商品分类过滤
        String category = (String) searchMap.get("category");
        if (category!=null&&!"".equals(category)){
            FilterQuery filterQuery  = new SimpleFilterQuery();
            Criteria filterCriteria = new Criteria("item_category");
            filterCriteria.is(category);
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
//        3.根据品牌过滤
        String brand = (String) searchMap.get("brand");
        if (brand!=null&&!"".equals(brand)){
            FilterQuery filterQuery  = new SimpleFilterQuery();
            Criteria filterCriteria = new Criteria("item_brand");
            filterCriteria.is(brand);
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
//        4.根据规格过滤
        Map<String,String> spec = (Map<String, String>) searchMap.get("spec");
        if (spec!=null&&spec.size()>0){
            for (String key : spec.keySet()) {
                FilterQuery filterQuery  = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_spec_"+key);
                filterCriteria.is(spec.get(key));
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
//        5.根据价格区间过滤
        String price = (String) searchMap.get("price");
        if (price!=null&&!"".equals(price)){

            String[] split = price.split("-");
            String min = split[0];
            String max = split[1];
            if (!"0".equals(min)){
                FilterQuery filterQuery  = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_price");
                filterCriteria.greaterThanEqual(min);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if (!"*".equals(max)){
                FilterQuery filterQuery  = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_price");
                filterCriteria.lessThanEqual(max);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }

        }
//        6.根据所选条件排序
        String sortField = (String) searchMap.get("sortField");
        String sort = (String) searchMap.get("sort");
        if (sortField!=null&&!"".equals(sortField)){
            if ("ASC".equals(sort)){
                query.addSort(new Sort(Sort.Direction.ASC, "item_"+sortField));
            }else {
                query.addSort(new Sort(Sort.Direction.DESC, "item_"+sortField));
            }

        }

        //设置分页查询
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo==null){
            pageNo = 1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize==null){
            pageSize = 20;
        }
        query.setOffset((pageNo-1)*pageSize);
        query.setRows(pageSize);

        //        设置高亮选项
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");
        highlightOptions.setSimplePrefix("<font color='red'>");
        highlightOptions.setSimplePostfix("</font>");
        query.setHighlightOptions(highlightOptions);

        //获得高亮查询结果
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        List<TbItem> items = page.getContent();
        for (TbItem item : items) {
            //得到高亮域
            List<HighlightEntry.Highlight> highlights = page.getHighlights(item);
            if (highlights!=null&&highlights.size()>0){
                HighlightEntry.Highlight highlight = highlights.get(0);
                //获得高亮片段
                List<String> snipplets = highlight.getSnipplets();
                if (snipplets!=null&&snipplets.size()>0){
                    String s = snipplets.get(0);
                    item.setTitle(s);
                }
            }
        }
        map.put("rows", items);
        map.put("totalPages", page.getTotalPages());
        map.put("total", page.getTotalElements());
        return map;
    }

    /**
     * 查询商品分类
     * @param searchMap
     * @return
     */
    private Map<String,Object> searchCategory(Map searchMap){
        Map<String,Object> map = new HashMap<>();


        Query query = new SimpleQuery();
        //按关键字搜搜哦
        Criteria criteria = null;
        String keywords = (String) searchMap.get("keywords");
        if (keywords!=null && !"".equals(keywords)){
            criteria = new Criteria("item_keywords");
            criteria.contains(keywords);
        }else{
            criteria = new Criteria();
            criteria.expression("*:*");
        }
        query.addCriteria(criteria);
        //添加分组选项
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> entries = groupResult.getGroupEntries();
        List<String> categoryList = new ArrayList<>();
        for (GroupEntry<TbItem> entry : entries) {
            String groupValue = entry.getGroupValue();
            categoryList.add(groupValue);
        }
        map.put("categoryList", categoryList);
        return map;

    }

    /**
     * redis获取品牌&规格列表
     * @param category
     * @return
     */
    private Map<String,Object> getBrandListAndSpecList(String category){
        Map<String,Object> map = new HashMap<>();
        if (!"".equals(category)){
            Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
            if (typeId!=null) {
                List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
                List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
                map.put("brandList", brandList);
                map.put("specList", specList);
            }
        }
        return map;
    }




}
