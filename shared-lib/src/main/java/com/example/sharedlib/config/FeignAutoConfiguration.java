package com.example.sharedlib.config;

import com.example.sharedlib.security.FeignClientInterceptor;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(RequestInterceptor.class)
public class FeignAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(RequestInterceptor.class)
    public RequestInterceptor feignClientInterceptor() {
        return new FeignClientInterceptor();
    }
}
