package com.quartz.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置
 *
 * @author Xuxu
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 当前跨域请求最大有效时长，这里默认30天
     */
    private static final long MAX_AGE = 30 * 24 * 60 * 60;

    /**
     * 静态资源文件映射配置
     * <p>
     * 注意 :
     * 1. addResourceHandler 参数可以有多个
     * 2. addResourceLocations 参数可以是多个，可以混合使用 file: 和 classpath : 资源路径
     * 3. addResourceLocations 参数中资源路径必须使用 / 结尾，如果没有此结尾则访问不到
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 自定义拦截
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MyInterceptor()).addPathPatterns("/**");
    }

    /**
     * 解决自定义过滤器跨域失效问题
     *
     * @return
     */
    private CorsConfiguration addCorsConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 设置访问源地址
        corsConfiguration.addAllowedOrigin("*");
        // 设置访问源请求头
        corsConfiguration.addAllowedHeader("*");
        // 设置访问源请求方法
        corsConfiguration.addAllowedMethod("*");
        // 服务器是否允许使用cookies
        corsConfiguration.setAllowCredentials(true);
        // 当前跨域请求最大有效时长（单位s）
        corsConfiguration.setMaxAge(MAX_AGE);
        return corsConfiguration;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对接口配置跨域设置
        source.registerCorsConfiguration("/**", addCorsConfig());
        return new CorsFilter(source);
    }

}
