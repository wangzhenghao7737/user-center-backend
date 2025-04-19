package com.zhenghao.usercenter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
//                允许跨域请求的域名
//                当credentials为true时，origins不能为星号[若ip不携带cookie，则无需设置为具体ip]
                .allowedOrigins("http://localhost:8080", "http://127.0.0.1:8080")
//                是否允许证书
                .allowCredentials(true)
//                允许的方法
                .allowedMethods("*")
//                跨域允许时间
                .maxAge(3600);
    }
}
