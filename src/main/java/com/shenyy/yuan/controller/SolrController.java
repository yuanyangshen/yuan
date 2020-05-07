package com.shenyy.yuan.controller;

import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/solr/")
public class SolrController {

    @GetMapping("getSolrList")
    public String  getSolrList(){

        /*HttpSolrServer httpSolrServer = new HttpSolrServer("http://localhost:8088/solr/new_core");
        //2.创建查询语句
        SolrQuery query = new SolrQuery();
        //3.设置查询条件
        query.setQuery("ProName:*电视*");*/
        return "HAHAH";
    }
}
