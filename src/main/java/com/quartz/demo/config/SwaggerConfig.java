package com.quartz.demo.config;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Swagger配置
 *
 * @author XiaoXuxuy
 */
@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
public class SwaggerConfig {

    /**
     * 定义分隔符,配置Swagger多包
     */
    private static final String SPLIT_STR = ";";

    /**
     * swagger2的配置文件，这里可以配置swagger2的一些基本的内容，比如扫描的包等等
     *
     * @return
     */
    @Bean
    public Docket createRestApi(SwaggerConfigProperties configProperties) {
        return new Docket(DocumentationType.SWAGGER_2)
                //切换环境时是否打开swagger
                .enable(configProperties.getEnable())
                .apiInfo(apiInfo(configProperties))
                .select()
                .apis(basePackage(configProperties.getBasePackage()))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(createGlobalParams());
    }

    private List<Parameter> createGlobalParams() {
        List<Parameter> parameters = new ArrayList<>();
        ParameterBuilder parameterBuilder = new ParameterBuilder();
        parameterBuilder.parameterType("header")
                .name("HEADER-USERINFO")
                .defaultValue("")
                .description("header中token字段")
                .modelRef(new ModelRef("string"))
                .required(false).build();

        parameters.add(parameterBuilder.build());
        return parameters;
    }

    /**
     * 该套 API 说明，包含作者、简介、版本、host、服务URL等
     *
     * @return
     */
    private ApiInfo apiInfo(SwaggerConfigProperties configProperties) {
        Contact contact = new Contact(configProperties.getAuthor(), null, null);
        return new ApiInfoBuilder()
                .title(configProperties.getTitle())
                .description(configProperties.getDescription())
                .contact(contact)
                .version(configProperties.getVersion())
                .build();
    }

    /**
     * 重写basePackage方法，使能够实现多包访问，
     *
     * @param basePackage
     * @return
     */
    public static Predicate<RequestHandler> basePackage(final String basePackage) {
        return input -> declaringClass(input).map(handlerPackage(basePackage)::apply).orElse(true);
    }

    private static Function<Class<?>, Boolean> handlerPackage(final String basePackage) {
        return input -> {
            // 循环判断匹配
            for (String strPackage : basePackage.split(SPLIT_STR)) {
                boolean isMatch = input.getPackage().getName().startsWith(strPackage);
                if (isMatch) {
                    return true;
                }
            }
            return false;
        };
    }

    private static Optional<Class<?>> declaringClass(RequestHandler input) {
        return Optional.ofNullable(input.declaringClass());
    }
}
