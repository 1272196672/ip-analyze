package com.lzx.service;

import com.lzx.entity.DangerousIp;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lzx.vo.Result;
import com.maxmind.geoip2.exception.GeoIp2Exception;

import java.io.IOException;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Bobby.zx.lin
 * @since 2023-03-08
 */
public interface DangerousIpService extends IService<DangerousIp> {

    /**
     * 根据一定时间内的访问数量来判断是否为危险主机
     *
     * @return {@link Result}
     * @throws IOException ioexception
     */
    Result distinctByNumInRange() throws IOException, GeoIp2Exception;

    /**
     * 根据一段时间内的访问频次来判断危险主机
     *
     * @return {@link Result}
     * @throws IOException ioexception
     */
    Result distinctByFrequent() throws IOException, GeoIp2Exception;

    /**
     * 获取一段时间内的危险IP数量
     *
     * @return {@link Result}
     */
    Result getNumOfIp();

    /**
     * 获取今日危险IP
     *
     * @return {@link Result}
     */
    Result getIpToday();


    /**
     * 获取近7日危险IP
     *
     * @return {@link Result}
     */
    Result getIp7days();
}
