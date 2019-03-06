package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;
import entity.PageResult;
import groupEntity.Specification;

import java.util.List;
import java.util.Map;

/**
 * @author qinshiji
 * @data 2019/1/11 15:26
 */
public interface SpecificationService {
    /**
     * 条件查询
     * @param specification
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageResult search(TbSpecification specification, Integer pageNum, Integer pageSize);

    /**
     * 新增规格
     * @param specification
     */
    void add(Specification specification);

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    Specification findOneById(Long id);

    /**
     * 更改规格
     * @param specification
     */
    void update(Specification specification);

    /**
     * 删除规格
     * @param ids
     */
    void delete(Long[] ids);

    List<Map> findSpecList();
}
