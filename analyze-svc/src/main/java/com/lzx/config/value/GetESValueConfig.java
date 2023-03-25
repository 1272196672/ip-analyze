package com.lzx.config.value;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cn.hutool.core.date.DateUtil;

/**
 * 得到ES配置
 *
 * @author Bobby.zx.lin
 * @date 2023/03/08
 */
@Configuration
public class GetESValueConfig {
    // ES
    public static String ES_HOSTNAME;

    public static Integer PORT;

    public static String SCHEME;

    public static String INDEX_NAME;

    public static Integer LOOP_MILLISECOND;

    public static Integer LOOP_LONGER_MILLISECOND;

    public static Integer DELETE_HOUR;

    public static Integer BUFFER_MILLISECOND;

    public static Long DANGEROUS_NUM;

    public static Integer DANGEROUS_FREQUENT;

    public static String GEOIP_MMDB_PATH;

    public static RestHighLevelClient client;

    public static String CLIENT_IP_KEYWORD;

    public static String CLIENT_GROUP;

    // svc-demo
    public static String SVC_HOSTNAME;

    public static String SVC_HOSTNAME_IPV6;


    @Value("${es.hostname}")
    public void getEsHostname(String hostname) {
        ES_HOSTNAME = hostname;
    }

    @Value("${es.port}")
    public void getEsPort(int port) {
        PORT = port;
    }

    @Value("${es.scheme}")
    public void getEsScheme(String scheme) {
        SCHEME = scheme;
    }

    @Value("${es.index-name}")
    public void getEsIndexName(String indexName) {
//        INDEX_NAME = indexName + DateUtil.format(DateUtil.date(), "yyyy.MM.dd");
        INDEX_NAME = indexName + "2023.03.17";
    }

    @Value("${es.loop-millisecond}")
    public void getEsLoopMillisecond(int loopMillisecond) {
        LOOP_MILLISECOND = loopMillisecond;
    }

    @Value("${es.loop-longer-millisecond}")
    public void getEsLoopLongerMillisecond(int loopLongerMillisecond) {
        LOOP_LONGER_MILLISECOND = loopLongerMillisecond;
    }

    @Value("${es.delete-hour}")
    public void getEsDeleteHour(int deleteHour) {
        DELETE_HOUR = deleteHour;
    }

    @Value("${es.buffer-millisecond}")
    public void getEsBufferMillisecond(int bufferMillisecond) {
        BUFFER_MILLISECOND = bufferMillisecond;
    }

    @Value("${es.dangerous-num}")
    public void getEsDangerousNum(long dangerousNum) {
        DANGEROUS_NUM = dangerousNum;
    }

    @Value("${es.dangerous-frequent}")
    public void getEsDangerousFrequent(int dangerousFrequent) {
        DANGEROUS_FREQUENT = dangerousFrequent;
    }

    @Value("${es.geoip-mmdb-path}")
    public void getEsGeoipMmdbPath(String geoipMmdbPath) {
        GEOIP_MMDB_PATH = geoipMmdbPath;
    }

    @Value("${es.keyword.client-ip}")
    public void getClientIpKeyword(String clientIpKeyword) {
        CLIENT_IP_KEYWORD = clientIpKeyword;
    }

    @Value("${es.keyword.group}")
    public void getEsClientGroup(String clientGroup) {
        CLIENT_GROUP = clientGroup;
    }


    @Value("${svc.hostname}")
    public void getSvcHostname(String hostname) {
        SVC_HOSTNAME = hostname;
    }

    @Value("${svc.ipv6.hostname}")
    public void getSvcHostnameIPV6(String hostname) {
        SVC_HOSTNAME_IPV6 = hostname;
    }

    /**
     * 创建客户端
     *
     * @return {@link RestHighLevelClient}
     */
    @Bean
    public void getEsClient() {
        client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(ES_HOSTNAME, PORT, SCHEME))
        );
    }
}
