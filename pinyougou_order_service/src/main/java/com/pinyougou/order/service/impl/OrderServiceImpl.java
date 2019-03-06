package com.pinyougou.order.service.impl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import groupEntity.Cart;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderExample.Criteria;


import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import utils.IdWorker;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbOrder> page=   (Page<TbOrder>) orderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private IdWorker idWorker;
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	@Autowired
    private TbPayLogMapper payLogMapper;

	/**
	 * 增加
	 */
	@Override
	public void add(TbOrder order) {

		List<Cart> cartList = (List<Cart>) redisTemplate.boundValueOps(order.getUserId()).get();
		if (cartList.size()<=0){
			throw  new RuntimeException("订单为空");
		}else {
			double totalMoney = 0.00;
			ArrayList<Long> list = new ArrayList<>();
			for (Cart cart : cartList) {
				TbOrder tbOrder = new TbOrder();
				long orderId = idWorker.nextId();
				//支付订单添加订单ID
				list.add(orderId);
				tbOrder.setOrderId(orderId);
				tbOrder.setUserId(order.getUserId());
				double payment = 0;
				tbOrder.setPaymentType(order.getPaymentType());
				tbOrder.setStatus("1");
				tbOrder.setCreateTime(new Date());
				tbOrder.setUpdateTime(new Date());
				tbOrder.setReceiverAreaName(order.getReceiverAreaName());
				tbOrder.setReceiver(order.getReceiver());
				tbOrder.setReceiverMobile(order.getReceiverMobile());
				tbOrder.setSourceType("2");
				tbOrder.setSellerId(cart.getSellerId());
				for (TbOrderItem orderItem : cart.getOrderItemList()) {
					payment+=orderItem.getNum()*orderItem.getPrice().doubleValue();
					orderItem.setId(idWorker.nextId());
					orderItem.setOrderId(orderId);
					orderItemMapper.insert(orderItem);
				}
				tbOrder.setPayment(new BigDecimal(payment));
				orderMapper.insert(tbOrder);
				//计算订单总金额
				totalMoney += payment;
			}
			//添加支付日志
			TbPayLog payLog = new TbPayLog();
			payLog.setUserId(order.getUserId());
			payLog.setCreateTime(new Date());
			payLog.setOutTradeNo(idWorker.nextId()+"");
			String orderList = list.toString();
			String replace = orderList.replace("[", "").replace("]", "");
			System.out.println(replace);
			payLog.setOrderList(replace);
			if ("1".equals(order.getPaymentType())){
				payLog.setPayType("1");
			}
			payLog.setTotalFee((long) (totalMoney*100));
			payLog.setTradeState("0");
			payLogMapper.insert(payLog);
			redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);
			//删除用户保存在redis中的购物车
			redisTemplate.delete(order.getUserId());
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order){
		orderMapper.updateByPrimaryKey(order);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbOrder findOne(Long id){
		return orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			orderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbOrderExample example=new TbOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(order!=null){			
						if(order.getPaymentType()!=null && order.getPaymentType().length()>0){
				criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}
			if(order.getPostFee()!=null && order.getPostFee().length()>0){
				criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}
			if(order.getStatus()!=null && order.getStatus().length()>0){
				criteria.andStatusLike("%"+order.getStatus()+"%");
			}
			if(order.getShippingName()!=null && order.getShippingName().length()>0){
				criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}
			if(order.getShippingCode()!=null && order.getShippingCode().length()>0){
				criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}
			if(order.getUserId()!=null && order.getUserId().length()>0){
				criteria.andUserIdLike("%"+order.getUserId()+"%");
			}
			if(order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0){
				criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}
			if(order.getBuyerNick()!=null && order.getBuyerNick().length()>0){
				criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}
			if(order.getBuyerRate()!=null && order.getBuyerRate().length()>0){
				criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}
			if(order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0){
				criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}
			if(order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}
			if(order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0){
				criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}
			if(order.getReceiver()!=null && order.getReceiver().length()>0){
				criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}
			if(order.getInvoiceType()!=null && order.getInvoiceType().length()>0){
				criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}
			if(order.getSourceType()!=null && order.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}
			if(order.getSellerId()!=null && order.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}
	
		}
		
		Page<TbOrder> page= (Page<TbOrder>)orderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
