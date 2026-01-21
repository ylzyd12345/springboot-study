package com.junmo.platform.base.constant;

/**
 * 消息常量
 *
 * @author junmo-platform
 * @version 1.0.0
 */
public interface MessageConstants {

    /**
     * 消息命名空间
     */
    String NAMESPACE = "junmo";

    /**
     * 消息分隔符
     */
    String DELIMITER = ".";

    /**
     * 用户相关消息
     */
    interface USER {

        /**
         * 用户创建消息主题
         */
        String CREATED_TOPIC = NAMESPACE + DELIMITER + "user" + DELIMITER + "created";

        /**
         * 用户更新消息主题
         */
        String UPDATED_TOPIC = NAMESPACE + DELIMITER + "user" + DELIMITER + "updated";

        /**
         * 用户删除消息主题
         */
        String DELETED_TOPIC = NAMESPACE + DELIMITER + "user" + DELIMITER + "deleted";

        /**
         * 用户登录消息主题
         */
        String LOGIN_TOPIC = NAMESPACE + DELIMITER + "user" + DELIMITER + "login";

        /**
         * 用户登出消息主题
         */
        String LOGOUT_TOPIC = NAMESPACE + DELIMITER + "user" + DELIMITER + "logout";

        /**
         * 用户状态变更消息主题
         */
        String STATUS_CHANGED_TOPIC = NAMESPACE + DELIMITER + "user" + DELIMITER + "status-changed";
    }

    /**
     * 通知相关消息
     */
    interface NOTIFICATION {

        /**
         * 通知消息主题
         */
        String TOPIC = NAMESPACE + DELIMITER + "notification";

        /**
         * 系统通知消息主题
         */
        String SYSTEM_TOPIC = TOPIC + DELIMITER + "system";

        /**
         * 邮件通知消息主题
         */
        String EMAIL_TOPIC = TOPIC + DELIMITER + "email";

        /**
         * 短信通知消息主题
         */
        String SMS_TOPIC = TOPIC + DELIMITER + "sms";

        /**
         * 站内信通知消息主题
         */
        String INBOX_TOPIC = TOPIC + DELIMITER + "inbox";
    }

    /**
     * 日志相关消息
     */
    interface LOG {

        /**
         * 操作日志消息主题
         */
        String OPERATION_TOPIC = NAMESPACE + DELIMITER + "log" + DELIMITER + "operation";

        /**
         * 登录日志消息主题
         */
        String LOGIN_TOPIC = NAMESPACE + DELIMITER + "log" + DELIMITER + "login";

        /**
         * 异常日志消息主题
         */
        String ERROR_TOPIC = NAMESPACE + DELIMITER + "log" + DELIMITER + "error";

        /**
         * 系统日志消息主题
         */
        String SYSTEM_TOPIC = NAMESPACE + DELIMITER + "log" + DELIMITER + "system";
    }

    /**
     * 系统相关消息
     */
    interface SYSTEM {

        /**
         * 系统启动消息主题
         */
        String STARTUP_TOPIC = NAMESPACE + DELIMITER + "system" + DELIMITER + "startup";

        /**
         * 系统关闭消息主题
         */
        String SHUTDOWN_TOPIC = NAMESPACE + DELIMITER + "system" + DELIMITER + "shutdown";

        /**
         * 系统健康检查消息主题
         */
        String HEALTH_CHECK_TOPIC = NAMESPACE + DELIMITER + "system" + DELIMITER + "health-check";

        /**
         * 系统配置更新消息主题
         */
        String CONFIG_UPDATED_TOPIC = NAMESPACE + DELIMITER + "system" + DELIMITER + "config-updated";
    }

    /**
     * 消息队列配置
     */
    interface QUEUE {

        /**
         * RabbitMQ交换机配置
         */
        interface RABBITMQ {

            /**
             * 用户交换机
             */
            String USER_EXCHANGE = "user.exchange";

            /**
             * 通知交换机
             */
            String NOTIFICATION_EXCHANGE = "notification.exchange";

            /**
             * 日志交换机
             */
            String LOG_EXCHANGE = "log.exchange";

            /**
             * 系统交换机
             */
            String SYSTEM_EXCHANGE = "system.exchange";
        }

        /**
         * Kafka主题配置
         */
        interface KAFKA {

            /**
             * 用户主题
             */
            String USER_TOPIC = "user-topic";

            /**
             * 通知主题
             */
            String NOTIFICATION_TOPIC = "notification-topic";

            /**
             * 日志主题
             */
            String LOG_TOPIC = "log-topic";

            /**
             * 系统主题
             */
            String SYSTEM_TOPIC = "system-topic";
        }
    }
}