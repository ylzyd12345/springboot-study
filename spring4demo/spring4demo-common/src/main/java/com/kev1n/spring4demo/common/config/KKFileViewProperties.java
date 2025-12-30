package com.kev1n.spring4demo.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * KKFileView 文件预览配置属性
 *
 * @author kev1n
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "kkfileview")
public class KKFileViewProperties {

    /**
     * KKFileView 服务地址
     */
    private String serverUrl = "http://localhost:8012";

    /**
     * 预览接口路径
     */
    private String previewPath = "/onlinePreview";

    /**
     * 是否使用缓存
     */
    private Boolean useCache = false;

    /**
     * 缓存过期时间（秒）
     */
    private Integer cacheExpireTime = 3600;

    /**
     * 当前服务地址（用于生成文件 URL）
     */
    private String currentServerUrl = "http://localhost:8080";

    /**
     * 是否强制更新缓存
     */
    private Boolean forceUpdateCache = true;
}