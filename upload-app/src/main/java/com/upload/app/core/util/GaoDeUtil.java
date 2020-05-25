package com.upload.app.core.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import cn.hutool.http.HttpUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GaoDeUtil {

    /**
     * Gets city by ip addr.
     *
     * @param ipAddr the ip addr
     *
     * @return the city by ip addr
     */
    public static GaodeLocation getCityByIpAddr(String ipAddr) {
        // http://lbs.amap.com/api/webservice/guide/api/ipconfig/
        log.info("getCityByIpAddr - 根据IP定位. ipAddr={}", ipAddr);
        GaodeLocation location = null;
        String urlAddressIp = "http://restapi.amap.com/v3/ip?key=f8bdce6f882a98635bb0b7b897331327&ip=%s";
        String url = String.format(urlAddressIp, ipAddr);
        try {
            String str = HttpUtil.get(url);
            //String str = HttpClientUtil.get(HttpConfig.custom().url(url));
            location = new ObjectMapper().readValue(str, GaodeLocation.class);
        } catch (Exception e) {
            log.error("getCityByIpAddr={}", e.getMessage(), e);
        }
        log.info("getCityByIpAddr - 根据IP定位. ipAddr={}, location={}", ipAddr, location);
        return location;
    }
}
