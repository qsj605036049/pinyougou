package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService {

    /**
     * 查询所有品牌
     */
    public List<TbBrand> findAll();

    PageResult findPage(Integer pageNum, Integer pageSize);

    void add(TbBrand brand);

    TbBrand findOne(Long id);

    void update(TbBrand brand);

    void delete(Long[] ids);

    PageResult search(TbBrand brand, Integer pageNum, Integer pageSize);

    List<Map> findBrandList();
}
