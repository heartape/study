package com.heartape;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.indices.IndexState;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.Map;

public class ESClient {
    public static void main(String[] args) throws IOException {
        // init
        RestClient restClient = RestClient.builder(new HttpHost("192.168.31.102", 9200)).build();
        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        ElasticsearchClient elasticsearchClient = new ElasticsearchClient(transport);
        // 异步
        // ElasticsearchAsyncClient elasticsearchAsyncClient = new ElasticsearchAsyncClient(transport);

        // index
        String SCHOOL = "school";
        ElasticsearchIndicesClient indices = elasticsearchClient.indices();
        // 检查索引
        boolean b = indices.exists(builder -> builder.index(SCHOOL)).value();
        if (!b){
            // 创建索引
            boolean acknowledged = indices.create(builder -> builder.index(SCHOOL)).acknowledged();
        }
        // 查询索引
        Map<String, IndexState> result = indices.get(builder -> builder.index(SCHOOL)).result();
        // 创建文档
        School firstSchool = new School("first school", "1231412", 19800);
        Result documentCreate = elasticsearchClient.create(builder -> builder.index(SCHOOL).id("1").document(firstSchool)).result();
        // 查询文档
        GetResponse<School> getResponse = elasticsearchClient.get(builder -> builder.index(SCHOOL).id("1"), School.class);
        // 更新文档
        School secondSchool = new School("second school", "1231412", 19800);
        String updateValue = elasticsearchClient.update(builder -> builder.index(SCHOOL).id("1").doc(secondSchool), School.class).result().jsonValue();
        // 删除文档
        String deleteValue = elasticsearchClient.delete(builder -> builder.index(SCHOOL).id("1")).result().jsonValue();
        // 删除索引
        boolean acknowledged = indices.delete(builder -> builder.index(SCHOOL)).acknowledged();
        transport.close();
    }
}
