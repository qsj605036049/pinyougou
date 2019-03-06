package com.pinyougou.user.controller;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pinyougou.user.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;

import entity.PageResult;
import entity.Result;
import utils.PhoneFormatCheckUtils;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll(){			
		return userService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return userService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbUser user,String smsCode){
		try {
			//检测验证码是否正确
			boolean flag = userService.checkSmsCode(user.getPhone(),smsCode);
			if (!flag){
				return new Result(false, "验证码错误");
			}
			userService.add(user);
			return new Result(true, "注册成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "注册失败");
		}
	}
	
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbUser user){
		try {
			userService.update(user);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbUser findOne(Long id){
		return userService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			userService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param user
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbUser user, int page, int rows  ){
		return userService.findPage(user, page, rows);		
	}

	/**
	 * 发送验证码
	 * @param phone
	 * @return
	 */
	@RequestMapping("/sendMsg")
	public Result sendMsg(String phone){
		try {
			//验证手机号
			boolean phoneLegal = PhoneFormatCheckUtils.isPhoneLegal(phone);
			if (!phoneLegal){
				return new Result(false, "手机号码非法");
			}
			userService.sendMsg(phone);
			return new Result(true, "验证码发送成功");
		} catch (IOException e) {
			e.printStackTrace();
			return new Result(false, "验证码发送失败 ");
		}
	}

	/**
	 * 获得登录名
	 * @return
	 */
	@RequestMapping("/getName")
	public Map<String,Object> getName(){
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		Map<String,Object> map = new HashMap<>();
		map.put("loginName", name);
		System.out.println(name);
		return map;
	}
	
}
