package com.kev1n.spring4demo.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

/**
 * S3 客户端配置类
 * 用于配置 RustFS 兼容的 S3 客户端
 *
 * @author kev1n
 * @since 1.0.0
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class S3ClientConfig {

    private final RustFSProperties rustFSProperties;

    @Bean
    public S3Client s3Client() {
        try {
            return S3Client.builder()
                    .endpointOverride(URI.create(rustFSProperties.getEndpoint()))
                    .region(Region.of(rustFSProperties.getRegion()))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(
                                            rustFSProperties.getAccessKey(),
                                            rustFSProperties.getSecretKey()
                                    )
                            )
                    )
                    .forcePathStyle(rustFSProperties.getPathStyleAccess())
                    .build();
        } catch (Exception e) {
            log.error("初始化 S3 客户端失败", e);
            throw new RuntimeException("初始化 S3 客户端失败: " + e.getMessage());
        }
    }
}