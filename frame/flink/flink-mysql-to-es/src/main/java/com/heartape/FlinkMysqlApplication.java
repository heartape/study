package com.heartape;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.elasticsearch.sink.Elasticsearch7SinkBuilder;
import org.apache.flink.connector.elasticsearch.sink.ElasticsearchSink;
import org.apache.flink.connector.elasticsearch.sink.FlushBackoffType;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class FlinkMysqlApplication {

    public final static String MYSQL_SERVER = "192.168.31.100";
    public final static String ES_SERVER = "192.168.31.100";

    public static void main(String[] args) {

        // 保存checkpoint的位置
        // String checkpointStorage = "file:///C:\\Users\\heartape\\Desktop";
        // String checkpointStorage = "file:///opt/flink/checkpoints/es";
        String checkpointStorage = args[0];

        // 读取checkpoint的位置，可以在命令行直接注入，不需要在java代码中操作
        // String source = "/af3d2ad769ad3b3a18ec768427ed66df/chk-6";
        // Configuration configuration = Configuration.fromMap(Map.of("execution.savepoint.path", checkpointStorage + source));
        // StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(configuration);

        Properties properties = new Properties();
        properties.setProperty("bigint.unsigned.handling.mode","long");
        properties.setProperty("decimal.handling.mode","double");
        properties.setProperty("dateConverters.format.date", "yyyy-MM-dd");
        properties.setProperty("dateConverters.format.timestamp.zone", "UTC+8");

        try (StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment()) {
            MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                    .hostname(MYSQL_SERVER)
                    .port(3306)
                    .databaseList("flink") // set captured database, If you need to synchronize the whole database, Please set tableList to ".*".
                    .tableList("flink.book") // set captured table
                    .username("root")
                    .password("root")
                    .debeziumProperties(properties)
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

            CheckpointConfig checkpointConfig = env.getCheckpointConfig();
            checkpointConfig.setCheckpointStorage(checkpointStorage);
            // 保留
            checkpointConfig.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

            env
                    // 每3000毫秒执行一次checkpoint
                    .enableCheckpointing(3000)
                    .setParallelism(1)
                    .fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MySQL Source")
                    .sinkTo(elasticsearchSink);
                    // .print("==>");

            env.execute("MySQL Binlog To Es");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static IndexRequest createIndexRequest(String element) {
        JsonObject jsonObject = JsonParser.parseString(element).getAsJsonObject();
        JsonObject after = jsonObject.getAsJsonObject("after");

        Gson gson = new Gson();
        Book book = gson.fromJson(after, Book.class);

        // id
        String id = book.getId().toString();
        return Requests.indexRequest()
                .index("book")
                .id(id)
                .source(book.toMap());
    }

    @Getter
    @Setter
    static class Book {
        private Long id;
        private String title;
        private BigDecimal price;
        private LocalDate publish_date;

        public Map<String, Object> toMap(){
            return Map.of("id", id.toString(), "title", title, "price", price, "publish_date", publish_date);
        }
    }
}
