package com.pinyougou.page.service.impl;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qinshiji
 * @data 2019/1/27 16:38
 */
@Component
public class AddPageListener implements MessageListener {
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Value("${pageDir}")
    private String pageDir;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
          String text = textMessage.getText();
            long goodsId = Long.parseLong(text);
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            Map<String,Object> dataModel = new HashMap<>();
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            List<TbItem> items = itemMapper.selectByExample(example);
            dataModel.put("goods", goods);
            dataModel.put("goodsDesc", goodsDesc);
            dataModel.put("items", items);

            Long category1Id = goods.getCategory1Id();
            TbItemCat itemCat1 = itemCatMapper.selectByPrimaryKey(category1Id);
            Long category2Id = goods.getCategory2Id();
            TbItemCat itemCat2 = itemCatMapper.selectByPrimaryKey(category2Id);
            Long category3Id = goods.getCategory3Id();
            TbItemCat itemCat3 = itemCatMapper.selectByPrimaryKey(category3Id);

            dataModel.put("itemCat1", itemCat1);
            dataModel.put("itemCat2", itemCat2);
            dataModel.put("itemCat3", itemCat3);

            for (TbItem item : items) {
                dataModel.put("item", item);
                Long itemId = item.getId();
                FileWriter out = new FileWriter(pageDir + itemId + ".html");
                template.process(dataModel,out);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
