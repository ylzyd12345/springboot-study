package com.junmo.platform.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Elasticsearch配置类
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.junmo.platform.core.repository.elasticsearch")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${elasticsearch.host:localhost}")
    private String host;

    @Value("${elasticsearch.port:9200}")
    private int port;

    @Value("${elasticsearch.username:}")
    private String username;

    @Value("${elasticsearch.password:}")
    private String password;

    @Value("${elasticsearch.protocol:http}")
    private String protocol;

    @Value("${elasticsearch.connection-timeout:5000}")
    private int connectionTimeout;

    @Value("${elasticsearch.socket-timeout:30000}")
    private int socketTimeout;

    /**
     * 配置ClientConfiguration（用于Spring Data Elasticsearch）
     *
     * @return ClientConfiguration
     */
    @Override
    public ClientConfiguration clientConfiguration() {
        String url = String.format("%s://%s:%d", protocol, host, port);

        var builder = ClientConfiguration.builder()
                .connectedTo(host + ":" + port)
                .withConnectTimeout(connectionTimeout)
                .withSocketTimeout(socketTimeout);

        // 配置认证信息
        if (username != null && !username.isEmpty()) {
            builder = builder.withBasicAuth(username, password);
        }

        return builder.build();
    }
}