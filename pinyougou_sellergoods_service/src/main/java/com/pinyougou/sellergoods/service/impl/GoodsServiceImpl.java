package com.pinyougou.sellergoods.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import groupEntity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbBrandMapper brandMapper;

	@Autowired
	private TbSellerMapper sellerMapper;

	@Autowired
	private TbItemMapper itemMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
     * @param goods
     */
	@Override
	public void add(Goods goods) {
		goods.getGoods().setAuditStatus("0");
		goods.getGoods().setIsMarketable("1");
		goodsMapper.insert(goods.getGoods());
		Long goodsId = goods.getGoods().getId();
		goods.getGoodsDesc().setGoodsId(goodsId);
		goodsDescMapper.insert(goods.getGoodsDesc());



		List<TbItem> items = goods.getItems();
		//设置图片
		String itemImages = goods.getGoodsDesc().getItemImages();
		List<Map> maps = JSON.parseArray(itemImages, Map.class);
		//sku categoryId
		Long category3Id = goods.getGoods().getCategory3Id();
		//sku sellerId
		String sellerId = goods.getGoods().getSellerId();
		//sku category
		TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(category3Id);
		String itemCatName = tbItemCat.getName();
		//sku brand
		Long brandId = goods.getGoods().getBrandId();
		TbBrand brand = brandMapper.selectByPrimaryKey(brandId);
		String brandName = brand.getName();
		//sku seller
		TbSeller tbSeller = sellerMapper.selectByPrimaryKey(sellerId);
		String nickName = tbSeller.getNickName();


		if ("1".equals(goods.getGoods().getIsEnableSpec())) {
			for (TbItem item : items) {
				String title = goods.getGoods().getGoodsName();
				String spec = item.getSpec();
				Map<String,String> object = JSON.parseObject(spec, Map.class);
				for (String s : object.keySet()) {
					title+=" "+object.get(s);
				}
				item.setTitle(title);
				setItemValue(goodsId,maps, category3Id, sellerId, itemCatName, brandName, nickName, item);
				itemMapper.insert(item);
			}
		}else {
			TbItem item = new TbItem();
			setItemValue(goodsId,maps, category3Id, sellerId, itemCatName, brandName, nickName, item);
			item.setTitle(goods.getGoods().getGoodsName());
			item.setSpec("{}");
//		 `price` decimal(20,2) NOT NULL COMMENT '商品价格，单位为：元',
			item.setPrice(goods.getGoods().getPrice());
//		 `num` int(10) NOT NULL COMMENT '库存数量',
			item.setNum(99999);
//		 `status` varchar(1) NOT NULL COMMENT '商品状态，1-正常，2-下架，3-删除',
			item.setStatus("1");
//		 `is_default` varchar(1) DEFAULT NULL,
			item.setIsDefault("1");
			itemMapper.insert(item);
		}


	}

	private void setItemValue(Long goodsId,List<Map> maps, Long category3Id, String sellerId, String itemCatName, String brandName, String nickName, TbItem item) {
		if (maps.size()>0){
			String image = (String) maps.get(0).get("url");
			item.setImage(image);
		}
		item.setCategoryid(category3Id);
		item.setSellerId(sellerId);
		item.setCategory(itemCatName);
		item.setBrand(brandName);
		item.setSeller(nickName);
		item.setCreateTime(new Date());
		item.setUpdateTime(new Date());
		item.setGoodsId(goodsId);
	}


	/**
	 * 修改
	 */
	@Override
	public void update(TbGoods goods){
		goodsMapper.updateByPrimaryKey(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbGoods findOne(Long id){
		return goodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(tbGoods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusEqualTo(goods.getAuditStatus());
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void setAuditStatus(Long[] ids, String status) {
		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}

	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Destination queueImportSolrDestination;
	@Autowired
	private Destination queueDeleteSolrDestination;
	@Autowired
	private Destination topicAddPageDestination;
	@Autowired
	private Destination topicDeletePageDestination;

	@Override
	public void setIsMarketable(Long[] ids, String isMarketable) {
		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			String auditStatus = tbGoods.getAuditStatus();
			if ("2".equals(auditStatus)){
				tbGoods.setIsMarketable(isMarketable);
				goodsMapper.updateByPrimaryKey(tbGoods);
//				如果商品上架
				if ("1".equals(isMarketable)){
//					发送导入索引库消息
					jmsTemplate.send(queueImportSolrDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(id+"");
						}
					});

//					发送生成商品详细页消息
					jmsTemplate.send(topicAddPageDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(id+"");
						}
					});
				}
//				如果商品下架
				if ("0".equals(isMarketable)){
//					发送删除索引库消息
					jmsTemplate.send(queueDeleteSolrDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(id+"");
						}
					});

//					发送删除商品详细页消息
					jmsTemplate.send(topicDeletePageDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(id+"");
						}
					});
				}

			}else {
				throw new RuntimeException("商品未审核不能上架");
			}
		}
	}

}
