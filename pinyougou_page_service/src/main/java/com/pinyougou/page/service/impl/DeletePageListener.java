package com.pinyougou.page.service.impl;

import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.File;
import java.util.List;

/**
 * @author qinshiji
 * @data 2019/1/27 16:48
 */
@Component
public class DeletePageListener implements MessageListener {
    @Autowired
    private TbItemMapper itemMapper;
    @Value("${pageDir}")
    private String pageDir;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String text = textMessage.getText();
            Long goodsId = Long.parseLong(text);
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            List<TbItem> items = itemMapper.selectByExample(example);
            for (TbItem item : items) {
                System.out.println(pageDir+item.getId()+".html");
                new File(pageDir+item.getId()+".html").delete();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
