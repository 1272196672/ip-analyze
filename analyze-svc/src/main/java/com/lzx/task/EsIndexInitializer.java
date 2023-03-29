package com.lzx.task;

import com.lzx.config.value.GetESValueConfig;
import com.lzx.service.DangerousIpService;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@PropertySource("classpath:application-dev.yml")
public class EsIndexInitializer {
    @Autowired
    private DangerousIpService dangerousIpService;

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

    @Scheduled(cron = "0/${es.loop-second} * * * * ?")
    public void loopDistinctByNumInRange() throws IOException, GeoIp2Exception {
        dangerousIpService.distinctByNumInRange();
    }

    @Scheduled(cron = "0/${es.loop-longer-second} * * * * ?")
    public void loopDistinctByFrequent() throws IOException, GeoIp2Exception {
        dangerousIpService.distinctByFrequent();
    }
}
