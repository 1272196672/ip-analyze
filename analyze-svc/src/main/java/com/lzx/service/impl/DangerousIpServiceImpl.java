package com.lzx.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lzx.config.value.GetESValueConfig;
import com.lzx.entity.DangerousIp;
import com.lzx.enums.DangerousType;
import com.lzx.mapper.DangerousIpMapper;
import com.lzx.service.DangerousIpService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzx.util.EsUtils;
import com.lzx.util.GetIpCountryUtils;
import com.lzx.vo.Result;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Bobby.zx.lin
 * @since 2023-03-08
 */
@Service
@Slf4j
public class DangerousIpServiceImpl extends ServiceImpl<DangerousIpMapper, DangerousIp> implements DangerousIpService {

    @Autowired
    private DangerousIpMapper dangerousIpMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Transactional
    @Override
    public Result distinctByNumInRange() throws IOException, GeoIp2Exception {
        RestHighLevelClient client = GetESValueConfig.client;
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .query(QueryBuilders.boolQuery()
//                        统计当前10s内的流量。配置缓冲，防止遗漏
                        .must(QueryBuilders.rangeQuery("@timestamp")
                                .gte(new Date().getTime() - GetESValueConfig.LOOP_MILLISECOND - GetESValueConfig.BUFFER_MILLISECOND))
//                        忽略服务器向外输出的流量
                        .mustNot(QueryBuilders.matchQuery(GetESValueConfig.CLIENT_IP_KEYWORD, GetESValueConfig.SVC_HOSTNAME))
                        .mustNot(QueryBuilders.matchQuery(GetESValueConfig.CLIENT_IP_KEYWORD, GetESValueConfig.SVC_HOSTNAME_IPV6)))
                .aggregation(AggregationBuilders
//                        按照客户端ip分组，方便后续统计
                        .terms(GetESValueConfig.CLIENT_GROUP).field(GetESValueConfig.CLIENT_IP_KEYWORD));
        SearchRequest request = EsUtils.getRequest(builder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        // logout
        HashSet<String> dangerousHost = new HashSet<>();
        log.info("[distinctByNumInRange] 查询耗时 = {}", response.getTook());
        SearchHits hits = response.getHits();
        log.info("[distinctByNumInRange] 总命中数 = {}", hits.getTotalHits());
        Terms terms = response.getAggregations().get(GetESValueConfig.CLIENT_GROUP);
        for (Terms.Bucket bucket : terms.getBuckets()) {
            String ip = bucket.getKeyAsString();
            log.info("[distinctByNumInRange] ip = {} 的命中数为 = {}", ip, bucket.getDocCount());
            if (bucket.getDocCount() > GetESValueConfig.DANGEROUS_NUM) {
                dangerousHost.add(ip);
                insertOrUpdateDangerousIp(ip, DangerousType.NUM.getType());
                log.info("[distinctByNumInRange] ip = {} 被识别为危险IP", ip);
            }
            if (!GetIpCountryUtils.isCN(ip)) {
                insertOrUpdateDangerousIp(ip, DangerousType.GEO.getType());
                log.info("[distinctByGeo] ip = {} 被识别为危险IP", ip);
            }
        }
        return Result.success(
                "本次查询得到的危险IP",
                new HashMap<String, HashSet<String>>() {{
                    put("ip", dangerousHost);
                }}
        );
    }

    @Transactional
    @Override
    public Result distinctByFrequent() throws IOException, GeoIp2Exception {
        RestHighLevelClient client = GetESValueConfig.client;
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .query(QueryBuilders.boolQuery()
//                        统计当前5min内的流量。配置缓冲，防止遗漏
                        .must(QueryBuilders.rangeQuery("@timestamp")
                                .gte(new Date().getTime() - GetESValueConfig.LOOP_LONGER_MILLISECOND - GetESValueConfig.BUFFER_MILLISECOND))
//                        忽略服务器向外输出的流量
                        .mustNot(QueryBuilders.matchQuery(GetESValueConfig.CLIENT_IP_KEYWORD, GetESValueConfig.SVC_HOSTNAME))
                        .mustNot(QueryBuilders.matchQuery(GetESValueConfig.CLIENT_IP_KEYWORD, GetESValueConfig.SVC_HOSTNAME_IPV6)))
                .aggregation(AggregationBuilders
//                        按照客户端ip分组，方便后续统计
                        .terms(GetESValueConfig.CLIENT_GROUP).field(GetESValueConfig.CLIENT_IP_KEYWORD));
        SearchRequest request = EsUtils.getRequest(builder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        // logout
        // use Redis
        HashSet<String> dangerousHost = new HashSet<>();
        Terms terms = response.getAggregations().get(GetESValueConfig.CLIENT_GROUP);
        log.info("[distinctByFrequent] 查询耗时 = {}", response.getTook());
        SearchHits hits = response.getHits();
        log.info("[distinctByFrequent] 总命中数 = {}", hits.getTotalHits());
        for (Terms.Bucket bucket : terms.getBuckets()) {
            String ip = bucket.getKeyAsString();
            log.info("[distinctByFrequent] ip = {} 的命中数为 = {}", ip, bucket.getDocCount());
            Integer frequent = (Integer) redisTemplate.opsForValue().get(ip);
            if (frequent == null) {
                frequent = 1;
            } else {
                frequent += 1;
            }
            redisTemplate.opsForValue().set(ip, frequent, GetESValueConfig.DELETE_HOUR, TimeUnit.HOURS);
            if (frequent > GetESValueConfig.DANGEROUS_FREQUENT) {
                dangerousHost.add(ip);
                insertOrUpdateDangerousIp(ip, DangerousType.FREQUENT.getType());
                log.info("[distinctByFrequent] ip = {} 被识别为危险IP", ip);
            }
            if (!GetIpCountryUtils.isCN(ip)) {
                insertOrUpdateDangerousIp(ip, DangerousType.GEO.getType());
                log.info("[distinctByGeo] ip = {} 被识别为危险IP", ip);
            }
        }
        return Result.success(
                "本次查询得到的危险IP",
                new HashMap<String, HashSet<String>>() {{
                    put("ip", dangerousHost);
                }}
        );
    }

    @Override
    @Transactional
    public Result getNumOfIp() {
        QueryWrapper<DangerousIp> queryWrapper = new QueryWrapper<>();
        long nowTime = DateUtil.date().getTime() - GetESValueConfig.LOOP_MILLISECOND - GetESValueConfig.BUFFER_MILLISECOND;
        LocalDateTime nowDate = LocalDateTimeUtil.of(nowTime);
        queryWrapper.gt("update_time", nowDate);
        Integer count = dangerousIpMapper.selectCount(queryWrapper);
        return Result.success("本次查询得到的危险IP的数量",
                new HashMap<String, Integer>() {{
                    put("ipNum", count);
                }}
        );
    }

    @Override
    public Result getIpToday() {
        QueryWrapper<DangerousIp> queryWrapper = new QueryWrapper<>();
        queryWrapper.gt("update_time", DateUtil.today());
        List<DangerousIp> ipList = dangerousIpMapper.selectList(queryWrapper);
        return Result.success("今日危险IP",
                new HashMap<String, List<DangerousIp>>() {{
                    put("ipList", ipList);
                }}
        );
    }

    @Override
    public Result getIp7days() {
        QueryWrapper<DangerousIp> queryWrapper = new QueryWrapper<>();
        queryWrapper.gt("update_time", DateUtil.offsetDay(DateUtil.beginOfDay(DateUtil.date()), -7));
        List<DangerousIp> ipList = dangerousIpMapper.selectList(queryWrapper);
        return Result.success("近7日危险IP",
                new HashMap<String, List<DangerousIp>>() {{
                    put("ipList7days", ipList);
                }}
        );
    }

    private void insertOrUpdateDangerousIp(String ip, int state) {
        DangerousIp dangerousIp = dangerousIpMapper.selectById(ip);
        if (dangerousIp == null) {
            dangerousIp = new DangerousIp();
            dangerousIp.setIp(ip);
            EsUtils.setState(dangerousIp, state);
            dangerousIpMapper.insert(dangerousIp);
        } else {
            EsUtils.setState(dangerousIp, state);
            dangerousIpMapper.updateById(dangerousIp);
        }
    }
}
