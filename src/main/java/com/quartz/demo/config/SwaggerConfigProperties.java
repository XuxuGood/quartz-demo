package com.quartz.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiaoxuxuy
 * @date 2021/6/2 1:21 下午
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "swagger")
public class SwaggerConfigProperties {
    private Boolean enable;
    private String basePackage;
    private String title;
    private String description;
    private String author;
    private String version;
}
