package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;

import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;

import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.sellergoods.service.SpecificationService;
import entity.PageResult;
import groupEntity.Specification;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author qinshiji
 * @data 2019/1/11 15:26
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {
    @Autowired
    private TbSpecificationMapper specificationMapper;
    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    /**
     * 条件查询
     * @param specification
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult search(TbSpecification specification, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        if (specification != null) {
            TbSpecificationExample example = new TbSpecificationExample();
            TbSpecificationExample.Criteria criteria = example.createCriteria();
            String specName = specification.getSpecName();
            if (specName != null && !"".equals(specName)) {
                criteria.andSpecNameLike("%" + specName + "%");
            }
            Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);
            return new PageResult(page.getTotal(),page.getResult());
        }
        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(null);
        return new PageResult(page.getTotal(),page.getResult() );

    }

    /**
     * 新增规格及规格选项
     * @param specification
     */
    @Override
    public void add(Specification specification) {
        specificationMapper.insert(specification.getSpecification());
        Long id = specification.getSpecification().getId();
        List<TbSpecificationOption> specificationOptions = specification.getSpecificationOptions();
        for (TbSpecificationOption specificationOption : specificationOptions) {
            specificationOption.setSpecId(id);
            specificationOptionMapper.insert(specificationOption);
        }
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @Override
    public Specification findOneById(Long id) {
        Specification specification = new Specification();
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<TbSpecificationOption> tbSpecificationOptions = specificationOptionMapper.selectByExample(example);
        specification.setSpecification(tbSpecification);
        specification.setSpecificationOptions(tbSpecificationOptions);

        return specification;
    }

    /**
     * 修改规格
     * @param specification
     */
    @Override
    public void update(Specification specification) {
        TbSpecification tbSpecification = specification.getSpecification();
        specificationMapper.updateByPrimaryKey(tbSpecification);
        Long specificationId = tbSpecification.getId();
//        删除规格选项
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(specificationId);
        specificationOptionMapper.deleteByExample(example);
//        添加
        List<TbSpecificationOption> specificationOptions = specification.getSpecificationOptions();
        for (TbSpecificationOption specificationOption : specificationOptions) {
            specificationOptionMapper.insert(specificationOption);
        }
    }

    /**
     * 删除规格
     * @param ids
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            specificationMapper.deleteByPrimaryKey(id);
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(id);
            specificationOptionMapper.deleteByExample(example);
        }
    }

    @Override
    public List<Map> findSpecList() {
        return specificationMapper.findSpecList();
    }
}
