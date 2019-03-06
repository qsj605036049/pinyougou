package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;

    @Override
    public List<TbBrand> findAll() {

        return brandMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        //基于pageHelper实现分页查询  (pageNum-1)*pageSize
        PageHelper.startPage(pageNum,pageSize);
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void add(TbBrand brand) {
        brandMapper.insert(brand);
    }

    @Override
    public TbBrand findOne(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(TbBrand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            brandMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public PageResult search(TbBrand brand, Integer pageNum, Integer pageSize) {
        //设置分页条件
        PageHelper.startPage(pageNum,pageSize);

        //构建查询条件对象
        TbBrandExample example = new TbBrandExample();
        if(brand!=null){
            //获取品牌名称查询条件
            String brandName = brand.getName();
            //criteria封装查询条件对象
            TbBrandExample.Criteria criteria = example.createCriteria();
            if(brandName!=null && !"".equals(brandName)){
                criteria.andNameLike("%"+brandName+"%");
            }

            //获取品牌首字母条件
            String firstChar = brand.getFirstChar();
            if(firstChar!=null && !"".equals(firstChar)){
                criteria.andFirstCharEqualTo(firstChar);
            }
        }
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);

        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public List<Map> findBrandList() {

        return brandMapper.findBrandList();
    }
}
