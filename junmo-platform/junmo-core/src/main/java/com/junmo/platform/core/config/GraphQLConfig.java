package com.junmo.platform.core.config;

import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * GraphQL配置类
 * 配置GraphQL端点和自定义标量类型
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Configuration
public class GraphQLConfig {

    /**
     * 配置GraphQL运行时
     * 注册自定义标量类型
     *
     * @return RuntimeWiringConfigurer
     */
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
            .scalar(GraphQLScalarType.newScalar()
                .name("LocalDateTime")
                .description("LocalDateTime标量类型")
                .coercing(new LocalDateTimeCoercing())
                .build());
    }

    /**
     * LocalDateTime类型转换器
     * 用于在GraphQL和Java之间转换LocalDateTime类型
     */
    private static class LocalDateTimeCoercing implements graphql.schema.Coercing<LocalDateTime, String> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public String serialize(Object dataFetcherResult) {
            if (dataFetcherResult instanceof LocalDateTime) {
                return ((LocalDateTime) dataFetcherResult).format(FORMATTER);
            }
            return null;
        }

        @Override
        public LocalDateTime parseValue(Object input) {
            if (input instanceof String) {
                return LocalDateTime.parse((String) input, FORMATTER);
            }
            return null;
        }

        @Override
        public LocalDateTime parseLiteral(Object input) {
            if (input instanceof String) {
                return LocalDateTime.parse((String) input, FORMATTER);
            }
            return null;
        }
    }
}