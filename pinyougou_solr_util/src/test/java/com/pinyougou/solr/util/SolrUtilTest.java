package com.pinyougou.solr.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author qinshiji
 * @data 2019/1/27 16:57
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext*.xml")
public class SolrUtilTest {

    @Autowired
    private SolrUtil solrUtil;

    @Test
    public void delete(){
        solrUtil.deleteSolr();
    }
}
