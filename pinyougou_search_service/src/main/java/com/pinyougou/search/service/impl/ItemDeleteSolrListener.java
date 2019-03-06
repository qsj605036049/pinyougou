package com.pinyougou.search.service.impl;

import com.pinyougou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.stereotype.Component;

import javax.jms.*;


/**
 * @author qinshiji
 * @data 2019/1/24 21:55
 */
@Component
public class ItemDeleteSolrListener implements MessageListener {
    @Autowired
    private SearchService searchService;
    @Autowired
    private SolrTemplate solrTemplate;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String id = textMessage.getText();
            System.out.println("监听消息"+id);
            SolrDataQuery query = new SimpleQuery("item_goodsid:"+id);
            solrTemplate.delete(query);
            solrTemplate.commit();
            System.out.println("删除索引库");
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
