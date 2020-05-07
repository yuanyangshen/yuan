package com.shenyy.yuan.util;

import com.shenyy.yuan.model.Content;
import com.shenyy.yuan.model.User;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolrUtil {
    //指定solr服务器的地址
    private final static String SOLR_URL = "http://localhost:8983/solr/new_core";

    /**
     * 创建SolrServer对象
     * <p>
     * 该对象有两个可以使用，都是线程安全的
     * 1、CommonsHttpSolrServer：启动web服务器使用的，通过http请求的
     * 2、 EmbeddedSolrServer：内嵌式的，导入solr的jar包就可以使用了
     * 3、solr 4.0之后好像添加了不少东西，其中CommonsHttpSolrServer这个类改名为HttpSolrClient
     *
     * @return
     */
    public HttpSolrClient createSolrServer() {
        HttpSolrClient solr = null;
        solr = new HttpSolrClient.Builder(SOLR_URL).withConnectionTimeout(10000).withSocketTimeout(60000).build();
        return solr;
    }


    /**
     * 往索引库添加文档
     *
     * @throws IOException
     * @throws SolrServerException
     */
    public void addDoc() throws IOException, SolrServerException {
        //构造一篇文档
        SolrInputDocument document = new SolrInputDocument();
        //往doc中添加字段,在客户端这边添加的字段必须在服务端中有过定义
        document.addField("id", "8");
        document.addField("cname", "周新星");
        document.addField("cdetail", "一个灰常牛逼的军事家");
        //获得一个solr服务端的请求，去提交  ,选择具体的某一个solr core
        HttpSolrClient solr = new HttpSolrClient.Builder(SOLR_URL).withConnectionTimeout(10000).withSocketTimeout(60000).build();
        solr.add(document);
        solr.commit();
        solr.close();
    }


    /**
     * 根据id从索引库删除文档
     */
    public void deleteDocumentById() throws Exception {
        //选择具体的某一个solr core
        HttpSolrClient server = new HttpSolrClient.Builder(SOLR_URL).withConnectionTimeout(10000).withSocketTimeout(60000).build();
        //删除文档
        server.deleteById("8");
        //删除所有的索引
        //solr.deleteByQuery("*:*");
        //提交修改
        server.commit();
        server.close();
    }

    /**
     * 查询
     *
     * @throws Exception
     */
    public void querySolr() throws Exception {
        HttpSolrClient solrServer = new HttpSolrClient.Builder(SOLR_URL).withConnectionTimeout(10000).withSocketTimeout(60000).build();
        SolrQuery query = new SolrQuery();
        //下面设置solr查询参数
        query.set("q", "风格");// 参数q  查询所有
        //query.set("q","周星驰");//相关查询，比如某条数据某个字段含有周、星、驰三个字  将会查询出来 ，这个作用适用于联想查询

        //参数fq, 给query增加过滤查询条件
        //query.addFilterQuery("id:[0 TO 9]");//id为0-4

        //给query增加布尔过滤条件
        //query.addFilterQuery("cdetail:演员");  //description字段中含有“演员”两字的数据

        //参数df,给query设置默认搜索域
        query.set("df", "content_keywords");
        query.set("qf", "cname^1 cdetail^0.1");
        query.set("bf", "sum(abs(cdetail))");

        //参数sort,设置返回结果的排序规则
        query.setSort("updatetime", SolrQuery.ORDER.desc);

        //设置分页参数
        query.setStart(0);
        query.setRows(20);//每一页多少值

        //参数hl,设置高亮
        query.setHighlight(true);
        query.setHighlightFragsize(500);
        //设置高亮的字段
        query.addHighlightField("cname");
        query.addHighlightField("cdetail");
        //设置高亮的样式
        query.setHighlightSimplePre("<font color='red'>");
        query.setHighlightSimplePost("</font>");

        //获取查询结果
        QueryResponse response = solrServer.query(query);
        //两种结果获取：得到文档集合或者实体对象

        //查询得到文档的集合
        SolrDocumentList solrDocumentList = response.getResults();
        Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
        //遍历列表
        for (SolrDocument doc : solrDocumentList) {
            String id = (String) doc.get("id");
            /*List<String> clist = highlighting.get(doc.get("id")).get("cdetail");
            if (clist != null && clist.size() > 0) {
                System.out.println(clist.get(0));
            }*/
            System.out.println(doc.get("cname"));
            System.out.println(doc.get("cdetail"));
        }

        //得到实体对象
        /*List<Content> tmpLists = response.getBeans(Content.class);
        if(tmpLists!=null && tmpLists.size()>0){
            System.out.println("通过文档集合获取查询的结果");
            for(Content content:tmpLists){
                System.out.println("id:"+content.getId()+"   name:"+content.getCname()+"    description:"+content.getCdetail());
            }
        }*/
    }

    public static void dataSyc() {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(SOLR_URL + "/dataimport?command=delta-import");
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(2000).setConnectTimeout(2000).build();
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);

            // 得到响应信息
            int statusCode = response.getStatusLine().getStatusCode();
            // 判断响应信息是否正确
            if (statusCode != HttpStatus.SC_OK) {
                // 终止并抛出异常
                httpGet.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        //SolrUtil solr = new SolrUtil();
//        solr.createSolrServer();
        //solr.addDoc();
        //solr.deleteDocumentById();
        //solr.querySolr();
        SolrUtil.dataSyc();

        /*String str = "开发规范<font color='red'>产品</font><font color='red'>介绍</font>FitServer R1200 V5是烽火公司基于Intel® Xeon® Scalable系列处理器平台最新推出的一款具有广泛用途的1U双路机架式服务器。凭借合理的设计，借助烽火";
        int i = str.indexOf("<font");
        int lenth = str.length();
        int start = 0;
        int end = 200;
        if (lenth <= 200){
            end =lenth;
        }
        String value = "";
        if (i >15){
            value = str.substring(i - 10,end);
        }else{
            value = str.substring(0,end);
        }
        System.out.println(value);*/
    }

}
