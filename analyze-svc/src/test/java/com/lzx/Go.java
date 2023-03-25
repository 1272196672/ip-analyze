package com.lzx;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lzx.config.value.GetESValueConfig;
import com.lzx.entity.DangerousIp;
import com.lzx.mapper.DangerousIpMapper;
import com.lzx.util.GetIpCountryUtils;
import com.lzx.vo.Result;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
public class Go {

    @Autowired
    private DangerousIpMapper dangerousIpMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void TestDate() {
        System.out.println(DateUtil.offsetDay(DateUtil.beginOfDay(DateUtil.date()), -7));
    }

    @Test
    public void TestDb() {
        QueryWrapper<DangerousIp> queryWrapper = new QueryWrapper<>();
        System.out.println("DateUtil.date() = " + DateUtil.date());
    }

    @Test
    public void TestRedis() {
//        redisTemplate.opsForValue().set(, 1);
    }

    @Test
    public void TestGeo() throws IOException, GeoIp2Exception {
        System.out.println("GetIpCountryUtils.getCounty() = " + GetIpCountryUtils.isCN("192.168.10.1"));
    }

    @Test
    public void CreateIndex() {
        try {
            RestHighLevelClient client = GetESValueConfig.client;
            if (!client.indices().exists(new GetIndexRequest(GetESValueConfig.INDEX_NAME), RequestOptions.DEFAULT)) {
                client.indices().create(new CreateIndexRequest(GetESValueConfig.INDEX_NAME), RequestOptions.DEFAULT);
            }
        } catch (Exception e) {
            System.out.println("EX");
        }
    }


}
