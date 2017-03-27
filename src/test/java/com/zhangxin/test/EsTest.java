package com.zhangxin.test;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.Date;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @author zhangxin
 *         Created on 17/3/16.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "classpath:spring/applicationContext.xml")
public class EsTest {
    private TransportClient client;

    @Before
    public void init() throws Exception {
        Settings settings = Settings.builder()
                .put("cluster.name", "usearch").build();
        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.32.64.19"), 9302))
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.32.64.20"), 9302));
    }

    @Test
    public void test() throws IOException {
        IndexResponse response = client.prepareIndex("test", "order", "2")
                .setSource(jsonBuilder()
                        .startObject()
                        .field("id", 2)
                        .field("aaa", "fuck测试你妹的aaaa")
                        .field("bbb", "bbb")
                        .field("ccc", "ccc")
                        .field("updateTime", new Date().getTime())
                        .endObject()
                )
                .get();

        client.close();

        System.out.println("here");
    }

    @Test
    public void query() {
        SearchRequestBuilder test = client.prepareSearch("test");

        test.setQuery(QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("id").lt(new Date().getTime())));

        System.out.println(test.get());

    }

    @Test
    public void delete() {
        DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .source("test")
                .filter(QueryBuilders.boolQuery().must(QueryBuilders.typeQuery("order")).filter(QueryBuilders.rangeQuery("id").lt(new Date().getTime())))
                .get();
    }


//    @Test
//    public void query() {
//        SearchResponse searchResponse = client.prepareSearch()
//                .setIndices("test")
//                .setTypes("order")
//                .setQuery(QueryBuilders.queryStringQuery("妹"))
//                .get();
//        System.out.println(searchResponse);
//    }
}

class Order implements Serializable {
    private int id;
    private String aaa;
    private String bbb;
    private String ccc;

    public Order() {
    }

    public Order(int id, String aaa, String bbb, String ccc) {
        this.id = id;
        this.aaa = aaa;
        this.bbb = bbb;
        this.ccc = ccc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAaa() {
        return aaa;
    }

    public void setAaa(String aaa) {
        this.aaa = aaa;
    }

    public String getBbb() {
        return bbb;
    }

    public void setBbb(String bbb) {
        this.bbb = bbb;
    }

    public String getCcc() {
        return ccc;
    }

    public void setCcc(String ccc) {
        this.ccc = ccc;
    }
}
