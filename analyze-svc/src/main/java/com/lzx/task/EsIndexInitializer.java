package com.lzx.task;

import com.lzx.config.value.GetESValueConfig;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EsIndexInitializer {
    @Scheduled(cron = "0 0 0 * * ?")
    public void initializeDailyIndex() {
        try {
            RestHighLevelClient client = GetESValueConfig.client;
            if (!client.indices().exists(new GetIndexRequest(GetESValueConfig.INDEX_NAME), RequestOptions.DEFAULT)) {
                client.indices().create(new CreateIndexRequest(GetESValueConfig.INDEX_NAME), RequestOptions.DEFAULT);
            }
        } catch (Exception e) {
            log.error("[Exception]: {}", e.toString());
        }
    }
}
