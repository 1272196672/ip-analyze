package com.lzx.util;

import com.lzx.config.value.GetESValueConfig;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

/**
 * 获得IP地址
 *
 * @author 林子翔
 * @date 2023/03/09
 */
public class GetIpCountryUtils {
    public static Boolean isCN(String ip) throws IOException, GeoIp2Exception {
        // 排除本地测试
        String substring = ip.substring(0, 7);
        if (substring.equals("192.168")) {
            return true;
        }

        File database = new File(GetESValueConfig.GEOIP_MMDB_PATH);

// This reader object should be reused across lookups as creation of it is
// expensive.
        DatabaseReader reader = new DatabaseReader.Builder(database).build();

// If you want to use caching at the cost of a small (~2MB) memory overhead:
// new DatabaseReader.Builder(file).withCache(new CHMCache()).build();

        InetAddress ipAddress = InetAddress.getByName(ip);

        CountryResponse response = reader.country(ipAddress);

        reader.close();

        return response.getCountry().getIsoCode().equals("CN");
    }
}
