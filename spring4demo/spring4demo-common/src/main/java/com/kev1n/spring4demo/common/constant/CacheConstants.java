package com.kev1n.spring4demo.common.constant;

/**
 * 缓存常量
 *
 * @author spring4demo
 * @version 1.0.0
 */
public interface CacheConstants {

    /**
     * 缓存命名空间
     */
    String NAMESPACE = "spring4demo";

    /**
     * 缓存分隔符
     */
    String DELIMITER = ":";

    /**
     * 用户相关缓存
     */
    interface USER {

        /**
         * 用户缓存key前缀
         */
        String KEY_PREFIX = NAMESPACE + DELIMITER + "user";

        /**
         * 用户信息缓存key
         * 格式: spring4demo:user:info:{userId}
         */
        String INFO_KEY = KEY_PREFIX + DELIMITER + "info";

        /**
         * 用户名缓存key
         * 格式: spring4demo:user:username:{username}
         */
        String USERNAME_KEY = KEY_PREFIX + DELIMITER + "username";

        /**
         * 用户邮箱缓存key
         * 格式: spring4demo:user:email:{email}
         */
        String EMAIL_KEY = KEY_PREFIX + DELIMITER + "email";

        /**
         * 用户手机号缓存key
         * 格式: spring4demo:user:phone:{phone}
         */
        String PHONE_KEY = KEY_PREFIX + DELIMITER + "phone";

        /**
         * 用户列表缓存key
         * 格式: spring4demo:user:list:{hash}
         */
        String LIST_KEY = KEY_PREFIX + DELIMITER + "list";

        /**
         * 用户缓存过期时间（秒）- 30分钟
         */
        long EXPIRE_TIME = 1800L;

        /**
         * 用户列表缓存过期时间（秒）- 5分钟
         */
        long LIST_EXPIRE_TIME = 300L;
    }

    /**
     * Token相关缓存
     */
    interface TOKEN {

        /**
         * Token缓存key前缀
         */
        String KEY_PREFIX = NAMESPACE + DELIMITER + "token";

        /**
         * 用户Token缓存key
         * 格式: spring4demo:token:{userId}
         */
        String USER_TOKEN_KEY = KEY_PREFIX + DELIMITER + "user";

        /**
         * Token黑名单key
         * 格式: spring4demo:token:blacklist:{token}
         */
        String BLACKLIST_KEY = KEY_PREFIX + DELIMITER + "blacklist";

        /**
         * Token过期时间（秒）- 24小时
         */
        long EXPIRE_TIME = 86400L;
    }

    /**
     * 权限相关缓存
     */
    interface PERMISSION {

        /**
         * 权限缓存key前缀
         */
        String KEY_PREFIX = NAMESPACE + DELIMITER + "permission";

        /**
         * 用户权限缓存key
         * 格式: spring4demo:permission:user:{userId}
         */
        String USER_PERMISSION_KEY = KEY_PREFIX + DELIMITER + "user";

        /**
         * 角色权限缓存key
         * 格式: spring4demo:permission:role:{roleId}
         */
        String ROLE_PERMISSION_KEY = KEY_PREFIX + DELIMITER + "role";

        /**
         * 权限缓存过期时间（秒）- 1小时
         */
        long EXPIRE_TIME = 3600L;
    }

    /**
     * 字典相关缓存
     */
    interface DICT {

        /**
         * 字典缓存key前缀
         */
        String KEY_PREFIX = NAMESPACE + DELIMITER + "dict";

        /**
         * 字典数据缓存key
         * 格式: spring4demo:dict:{dictType}
         */
        String DICT_DATA_KEY = KEY_PREFIX + DELIMITER + "data";

        /**
         * 字典缓存过期时间（秒）- 1小时
         */
        long EXPIRE_TIME = 3600L;
    }

    /**
     * 验证码相关缓存
     */
    interface CAPTCHA {

        /**
         * 验证码缓存key前缀
         */
        String KEY_PREFIX = NAMESPACE + DELIMITER + "captcha";

        /**
         * 登录验证码缓存key
         * 格式: spring4demo:captcha:login:{uuid}
         */
        String LOGIN_KEY = KEY_PREFIX + DELIMITER + "login";

        /**
         * 邮箱验证码缓存key
         * 格式: spring4demo:captcha:email:{email}
         */
        String EMAIL_KEY = KEY_PREFIX + DELIMITER + "email";

        /**
         * 手机验证码缓存key
         * 格式: spring4demo:captcha:phone:{phone}
         */
        String PHONE_KEY = KEY_PREFIX + DELIMITER + "phone";

        /**
         * 验证码过期时间（秒）- 5分钟
         */
        long EXPIRE_TIME = 300L;
    }

    /**
     * 限流相关缓存
     */
    interface RATE_LIMIT {

        /**
         * 限流缓存key前缀
         */
        String KEY_PREFIX = NAMESPACE + DELIMITER + "ratelimit";

        /**
         * API限流缓存key
         * 格式: spring4demo:ratelimit:api:{apiPath}
         */
        String API_KEY = KEY_PREFIX + DELIMITER + "api";

        /**
         * 用户限流缓存key
         * 格式: spring4demo:ratelimit:user:{userId}
         */
        String USER_KEY = KEY_PREFIX + DELIMITER + "user";

        /**
         * IP限流缓存key
         * 格式: spring4demo:ratelimit:ip:{ip}
         */
        String IP_KEY = KEY_PREFIX + DELIMITER + "ip";

        /**
         * 限流窗口时间（秒）- 1分钟
         */
        long WINDOW_TIME = 60L;
    }

    /**
     * 分布式锁相关缓存
     */
    interface LOCK {

        /**
         * 锁缓存key前缀
         */
        String KEY_PREFIX = NAMESPACE + DELIMITER + "lock";

        /**
         * 用户锁key
         * 格式: spring4demo:lock:user:{userId}
         */
        String USER_LOCK_KEY = KEY_PREFIX + DELIMITER + "user";

        /**
         * 订单锁key
         * 格式: spring4demo:lock:order:{orderId}
         */
        String ORDER_LOCK_KEY = KEY_PREFIX + DELIMITER + "order";

        /**
         * 锁过期时间（秒）- 30秒
         */
        long EXPIRE_TIME = 30L;

        /**
         * 锁等待时间（毫秒）- 10秒
         */
        long WAIT_TIME = 10000L;
    }
}