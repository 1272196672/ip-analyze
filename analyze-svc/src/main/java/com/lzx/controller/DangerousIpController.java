package com.lzx.controller;


import com.lzx.service.DangerousIpService;
import com.lzx.vo.Result;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Bobby.zx.lin
 * @since 2023-03-08
 */
@RestController
@RequestMapping("/dangerous-ip")
@CrossOrigin
public class DangerousIpController {
    @Autowired
    private DangerousIpService dangerousIpService;

    @GetMapping("/by-num")
    public Result distinctByNumInRange() throws IOException, GeoIp2Exception {
        return dangerousIpService.distinctByNumInRange();
    }

    @GetMapping("/by-frequent")
    public Result distinctByFrequent() throws IOException, GeoIp2Exception {
        return dangerousIpService.distinctByFrequent();
    }

    @GetMapping("/get-num")
    public Result getNumOfIp() {
        return dangerousIpService.getNumOfIp();
    }

    @GetMapping("/get-ip-today")
    public Result getIpToday() {
        return dangerousIpService.getIpToday();
    }

    @GetMapping("/get-ip-7days")
    public Result getIp7days() {
        return dangerousIpService.getIp7days();
    }
}
