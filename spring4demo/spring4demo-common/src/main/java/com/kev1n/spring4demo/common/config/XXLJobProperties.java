package com.kev1n.spring4demo.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * XXL-Job 配置属性类
 * 
 * <p>用于从配置文件中读取 XXL-Job 相关配置，包括调度中心地址、访问令牌、执行器配置等。</p>
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "xxl.job")
public class XXLJobProperties {

    /**
     * 调度中心配置
     */
    private Admin admin = new Admin();

    /**
     * 执行器配置
     */
    private Executor executor = new Executor();

    /**
     * 调度中心配置
     */
    @Data
    public static class Admin {
        /**
         * 调度中心部署跟地址 [选填]：如调度中心集群部署存在多个地址则用逗号分隔
         * 执行器将会使用该地址进行"执行器心跳注册"和"任务结果回调"
         * 为空则关闭自动注册
         */
        private String addresses;

        /**
         * 执行器通讯TOKEN [选填]：非空时启用
         */
        private String accessToken = "default_token";
    }

    /**
     * 执行器配置
     */
    @Data
    public static class Executor {
        /**
         * 执行器AppName [选填]：执行器心跳注册分组依据；为空则关闭自动注册
         */
        private String appname;

        /**
         * 执行器注册 [选填]：优先使用该配置作为注册地址
         * 为空时使用内嵌服务 "IP:PORT" 作为注册地址
         */
        private String address;

        /**
         * 执行器IP [选填]：默认为空表示自动获取IP
         * 多网卡时可手动设置指定IP，该IP不会绑定Host仅作为通讯实用
         * 地址信息用于 "执行器注册" 和 "调度中心请求并触发任务"
         */
        private String ip;

        /**
         * 执行器端口号 [选填]：小于等于0则自动获取；默认端口为9999
         * 单机部署多个执行器时，注意要配置不同执行器端口
         */
        private int port = 9999;

        /**
         * 执行器运行日志文件存储磁盘路径 [选填]：需要对该路径拥有读写权限
         * 为空则使用默认路径
         */
        private String logpath = "/data/applogs/xxl-job/jobhandler";

        /**
         * 执行器日志文件保存天数 [选填]：过期日志自动清理
         * 限制值大于等于3时生效; 否则, 如-1, 关闭自动清理功能
         */
        private int logretentiondays = 30;
    }
}