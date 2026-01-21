package com.junmo.platform.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * RustFS 文件存储配置属性
 *
 * @author junmo
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
     * 必须从配置文件或环境变量中设置，不能使用默认值
     */
    private String accessKey;

    /**
     * 秘密密钥
     * 必须从配置文件或环境变量中设置，不能使用默认值
     */
    private String secretKey;

    /**
     * 存储桶名称
     */
    private String bucketName = "junmo-platform";

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