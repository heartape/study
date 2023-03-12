package com.heartape;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.elasticsearch.sink.Elasticsearch7SinkBuilder;
import org.apache.flink.connector.elasticsearch.sink.ElasticsearchSink;
import org.apache.flink.connector.elasticsearch.sink.FlushBackoffType;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Map;

public class FlinkMysqlApplication {

    public final static String MYSQL_SERVER = "192.168.31.200";
    public final static String ES_SERVER = "192.168.31.200";

    public static void main(String[] args) {
        try (StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment()) {
            MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                    .hostname(MYSQL_SERVER)
                    .port(3306)
                    .databaseList("flink") // set captured database, If you need to synchronize the whole database, Please set tableList to ".*".
                    .tableList("flink.book") // set captured table
                    .username("root")
                    .password("root")
                    .deserializer(new JsonDebeziumDeserializationSchema()) // converts SourceRecord to JSON String
                    .startupOptions(StartupOptions.initial())
                    .build();

            ElasticsearchSink<String> elasticsearchSink = new Elasticsearch7SinkBuilder<String>()
                    // 下面的设置使 sink 在接收每个元素之后立即提交，否则这些元素将被缓存起来
                    .setBulkFlushMaxActions(1)
                    .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                    .setHosts(new HttpHost(ES_SERVER, 9200, "http"))
                    .setEmitter((element, context, indexer) -> indexer.add(createIndexRequest(element)))
                    // 这里启用了一个指数退避重试策略，初始延迟为 1000 毫秒且最大重试次数为 5
                    .setBulkFlushBackoffStrategy(FlushBackoffType.EXPONENTIAL, 3, 1000)
                    .build();

            env
                    .setParallelism(1)
                    // 每5000毫秒执行一次checkpoint,需要kafka
                    // .enableCheckpointing(5000)
                    .fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MySQL Source")
                    .sinkTo(elasticsearchSink);
            // .print();

            env.execute("Print MySQL Binlog");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private static IndexRequest createIndexRequest(String element) {
        JsonObject jsonObject = JsonParser.parseString(element).getAsJsonObject();
        JsonObject after = jsonObject.getAsJsonObject("after");
        // id
        String id = after.get("id").getAsString();
        // map
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();

        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(after, type);

        // 时间处理，cdc将时间改为了时间戳
        int publishDate = after.get("publish_date").getAsInt();
        LocalDate localDate = LocalDate.ofEpochDay(publishDate);
        map.put("publish_date", localDate);

        return Requests.indexRequest()
                .index("book")
                .id(id)
                .source(map);
    }
}
