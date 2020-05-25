package com.upload.app.config.web;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @Author: 杨忠豪
 * @Description: 跨域设置
 * @Date: Created in 10:40 2018/4/12
 * @Modified By:
 */
@Configuration
@Data
@Slf4j
public class CorsFilterConfig {

    private Boolean allowCredentials = true;
    private String[] allowedOrigin = new String[]{"*"};
    private String[] allowedHeader = new String[]{"*"};
    private String maxAge="1000";
    private String[] allowedMethod = new String[]{"OPTIONS","HEAD","GET","PUT","POST","DELETE","PATCH"};

    @Bean(name = "CorsFilter")
    public CorsFilter corsFilter() {

        log.info(">>>>>>>>>>>>>>>跨域配置<<<<<<<<<<<<<<alowCredentials:" + allowCredentials +
                ", allowedOrigin:" + allowedOrigin + ", allowedHeader:" + allowedHeader
                + ", maxAge:" + maxAge + ", allowedMethod:" + allowedMethod);

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();

        // 允许cookies跨域
        config.setAllowCredentials(allowCredentials);

        // #允许向该服务器提交请求的URI，*表示全部允许，在SpringMVC中，如果设成*，会自动转成当前请求头中的Origin
        for(String origin : allowedOrigin){
            config.addAllowedOrigin(origin);
        }

        // #允许访问的头信息,*表示全部
        for (String header: allowedHeader) {
            config.addAllowedHeader(header);
        }

        // 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再预检了
        config.setMaxAge(Long.parseLong(maxAge));

        // 允许提交请求的方法，*表示全部允许
        for (String method : allowedMethod){
            config.addAllowedMethod(method);
        }
//        config.addAllowedMethod("OPTIONS");
//        config.addAllowedMethod("HEAD");
//        // 允许Get的请求方法
//        config.addAllowedMethod("GET");
//        config.addAllowedMethod("PUT");
//        config.addAllowedMethod("POST");
//        config.addAllowedMethod("DELETE");
//        config.addAllowedMethod("PATCH");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
