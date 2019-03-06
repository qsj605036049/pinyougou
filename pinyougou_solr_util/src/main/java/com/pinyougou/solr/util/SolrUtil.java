package com.pinyougou.solr.util;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * solr工具类
 * @author qinshiji
 * @data 2019/1/23 15:41
 */
@Component
public class SolrUtil {
    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;



    public void importSolr(){
        List<TbItem> items = itemMapper.findAllGrounding();
        for (TbItem item : items) {
            String spec = item.getSpec();
            Map<String,String> map = JSON.parseObject(spec, Map.class);
            item.setSpecMap(map);
        }
        solrTemplate.saveBeans(items);
        solrTemplate.commit();

    }

    public void deleteSolr(){
        SolrDataQuery query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
