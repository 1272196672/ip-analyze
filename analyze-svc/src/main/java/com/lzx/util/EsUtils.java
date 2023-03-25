package com.lzx.util;

import com.lzx.config.value.GetESValueConfig;
import com.lzx.entity.DangerousIp;
import com.lzx.enums.DangerousType;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;

@Slf4j
public class EsUtils {
    /**
     * @return {@link SearchRequest }
     * @author 林子翔
     * @description 获取builder查询条件，构造并返回请求
     * @since 2022/08/18
     */
    public static SearchRequest getRequest(SearchSourceBuilder searchSourceBuilder) {
        //            获得builder
        SearchRequest request = new SearchRequest();
        request.indices(GetESValueConfig.INDEX_NAME);
        request.source(searchSourceBuilder);
        return request;
    }

    public static DangerousIp setState(DangerousIp dangerousIp, int state){
        if (state == DangerousType.FREQUENT.getType()) {
            dangerousIp.setStateFrequent(true);
        } else if (state == DangerousType.NUM.getType()) {
            dangerousIp.setStateNum(true);
        } else if (state == DangerousType.GEO.getType()) {
            dangerousIp.setStateGeo(true);
        }
        return dangerousIp;
    }
}

