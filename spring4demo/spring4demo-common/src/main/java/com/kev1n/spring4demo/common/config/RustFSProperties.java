package com.kev1n.spring4demo.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * RustFS 文件存储配置属性
 *
 * @author kev1n
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "rustfs")
public class RustFSProperties {

    /**
     * RustFS 服务端点地址
     */
    private String endpoint = "http://localhost:9000";

    /**
     * 访问密钥
     */
    private String accessKey = "admin";

    /**
     * 秘密密钥
     */
    private String secretKey = "admin123";

    /**
     * 存储桶名称
     */
    private String bucketName = "spring4demo";

    /**
     * 区域
     */
    private String region = "us-east-1";

    /**
     * 是否启用路径风格访问
     */
    private Boolean pathStyleAccess = true;

    /**
     * 最大文件大小（MB）
     */
    private Long maxFileSize = 10L;

    /**
     * 最大请求大小（MB）
     */
    private Long maxRequestSize = 100L;
}