package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

/**
 * @author qinshiji
 * @data 2019/1/24 21:29
 */
@Component
public class ItemImportSolrListener implements MessageListener {

    @Autowired
    private SearchService searchService;

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * MQ接收信息导入solr
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String text = textMessage.getText();
            Long id= Long.parseLong(text);
            System.out.println("监听消息"+id);
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(id);
            List<TbItem> items = itemMapper.selectByExample(example);
            solrTemplate.saveBeans(items);
            solrTemplate.commit();
            System.out.println("导入完成");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
