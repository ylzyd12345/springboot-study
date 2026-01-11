package com.kev1n.spring4demo.core.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 用户缓存服务
 * <p>
 * 实现多级缓存策略（L1本地缓存 + L2分布式缓存），提供缓存穿透、缓存击穿、缓存雪崩防护。
 * </p>
 *
 * <p>缓存策略：</p>
 * <ul>
 *   <li>L1缓存：Caffeine本地缓存，高性能，容量有限</li>
 *   <li>L2缓存：Redis分布式缓存，容量大，支持集群</li>
 *   <li>Cache-Aside模式：先查缓存，缓存未命中则查数据库并回填缓存</li>
 * </ul>
 *
 * <p>防护措施：</p>
 * <ul>
 *   <li>缓存穿透：缓存空值（TODO：可使用布隆过滤器优化）</li>
 *   <li>缓存击穿：使用互斥锁（ReentrantLock）防止大量请求同时穿透到数据库</li>
 *   <li>缓存雪崩：设置随机过期时间（TODO：可在Redis配置中实现）</li>
 * </ul>
 *
 * @author spring4demo
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCacheService {

    private final UserMapper userMapper;
    private final Cache<String, Object> caffeineCache;
    private final RedisTemplate<String, Object> redisTemplate;

    // 缓存key前缀
    private static final String CACHE_KEY_PREFIX = "user:";
    private static final String CACHE_KEY_NULL = "user:null:";

    // 缓存过期时间
    private static final long CACHE_EXPIRE_TIME = 30; // 30分钟

    // 互斥锁，防止缓存击穿
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * 从缓存中获取用户（多级缓存）
     * <p>
     * 查询顺序：
     * 1. 先查Caffeine本地缓存
     * 2. 再查Redis分布式缓存
     * 3. 最后查数据库
     * </p>
     *
     * @param userId 用户ID
     * @return 用户对象，如果不存在则返回null
     */
    public User getUserFromCache(Long userId) {
        if (userId == null) {
            return null;
        }

        String cacheKey = CACHE_KEY_PREFIX + userId;

        // 1. 先查Caffeine本地缓存
        User user = (User) caffeineCache.getIfPresent(cacheKey);
        if (user != null) {
            log.debug("从Caffeine缓存获取用户: userId={}", userId);
            return user;
        }

        // 2. 再查Redis分布式缓存
        user = (User) redisTemplate.opsForValue().get(cacheKey);
        if (user != null) {
            log.debug("从Redis缓存获取用户: userId={}", userId);
            // 回填到Caffeine缓存
            caffeineCache.put(cacheKey, user);
            return user;
        }

        // 3. 最后查数据库（使用互斥锁防止缓存击穿）
        try {
            lock.lock();
            // 双重检查，防止多个线程同时查询数据库
            user = (User) caffeineCache.getIfPresent(cacheKey);
            if (user != null) {
                return user;
            }

            user = (User) redisTemplate.opsForValue().get(cacheKey);
            if (user != null) {
                caffeineCache.put(cacheKey, user);
                return user;
            }

            // 查询数据库
            user = userMapper.selectById(userId);
            if (user != null) {
                log.debug("从数据库获取用户: userId={}", userId);
                // 写入Redis缓存
                redisTemplate.opsForValue().set(cacheKey, user, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
                // 写入Caffeine缓存
                caffeineCache.put(cacheKey, user);
            } else {
                // 缓存空值，防止缓存穿透
                String nullKey = CACHE_KEY_NULL + userId;
                redisTemplate.opsForValue().set(nullKey, null, 5, TimeUnit.MINUTES);
                log.debug("缓存空值，防止缓存穿透: userId={}", userId);
            }

            return user;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 将用户放入缓存
     * <p>
     * 同时更新Caffeine和Redis缓存
     * </p>
     *
     * @param user 用户对象
     */
    public void putUserToCache(User user) {
        if (user == null || user.getId() == null) {
            return;
        }

        String cacheKey = CACHE_KEY_PREFIX + user.getId();

        // 写入Redis缓存
        redisTemplate.opsForValue().set(cacheKey, user, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);

        // 写入Caffeine缓存
        caffeineCache.put(cacheKey, user);

        log.debug("用户已放入缓存: userId={}", user.getId());
    }

    /**
     * 更新用户缓存
     * <p>
     * 先更新数据库，再删除缓存（Cache-Aside模式）
     * </p>
     *
     * @param user 用户对象
     */
    public void updateUserInCache(User user) {
        if (user == null || user.getId() == null) {
            return;
        }

        String cacheKey = CACHE_KEY_PREFIX + user.getId();

        // 删除Caffeine缓存
        caffeineCache.invalidate(cacheKey);

        // 删除Redis缓存
        redisTemplate.delete(cacheKey);

        // 删除空值缓存
        String nullKey = CACHE_KEY_NULL + user.getId();
        redisTemplate.delete(nullKey);

        log.debug("用户缓存已删除: userId={}", user.getId());
    }

    /**
     * 从缓存中删除用户
     * <p>
     * 同时删除Caffeine和Redis缓存
     * </p>
     *
     * @param userId 用户ID
     */
    public void deleteUserFromCache(Long userId) {
        if (userId == null) {
            return;
        }

        String cacheKey = CACHE_KEY_PREFIX + userId;

        // 删除Caffeine缓存
        caffeineCache.invalidate(cacheKey);

        // 删除Redis缓存
        redisTemplate.delete(cacheKey);

        // 删除空值缓存
        String nullKey = CACHE_KEY_NULL + userId;
        redisTemplate.delete(nullKey);

        log.debug("用户缓存已删除: userId={}", userId);
    }

    /**
     * 批量从缓存中获取用户
     * <p>
     * TODO: 待实现批量缓存查询功能
     * </p>
     *
     * @param userIds 用户ID列表
     * @return 用户列表
     */
    public List<User> getUsersFromCache(List<Long> userIds) {
        // TODO: 待实现批量缓存查询功能
        log.warn("批量缓存查询功能待实现: userIds={}", userIds);
        return null;
    }

    /**
     * 批量将用户放入缓存
     * <p>
     * TODO: 待实现批量缓存写入功能
     * </p>
     *
     * @param users 用户列表
     */
    public void putUsersToCache(List<User> users) {
        // TODO: 待实现批量缓存写入功能
        log.warn("批量缓存写入功能待实现: users={}", users != null ? users.size() : 0);
    }

    /**
     * 批量删除用户缓存
     * <p>
     * TODO: 待实现批量缓存删除功能
     * </p>
     *
     * @param userIds 用户ID列表
     */
    public void deleteUsersFromCache(List<Long> userIds) {
        // TODO: 待实现批量缓存删除功能
        log.warn("批量缓存删除功能待实现: userIds={}", userIds);
    }

    /**
     * 清空所有用户缓存
     * <p>
     * 清空Caffeine和Redis中的所有用户缓存
     * </p>
     */
    public void clearAllUserCache() {
        // 清空Caffeine缓存
        caffeineCache.invalidateAll();

        // 清空Redis缓存（使用通配符删除）
        redisTemplate.delete(redisTemplate.keys(CACHE_KEY_PREFIX + "*"));

        // 清空空值缓存
        redisTemplate.delete(redisTemplate.keys(CACHE_KEY_NULL + "*"));

        log.info("所有用户缓存已清空");
    }

    /**
     * 预热缓存
     * <p>
     * TODO: 待实现缓存预热功能
     * </p>
     *
     * @param userIds 用户ID列表
     */
    public void warmUpCache(List<Long> userIds) {
        // TODO: 待实现缓存预热功能
        log.warn("缓存预热功能待实现: userIds={}", userIds);
    }

    /**
     * TODO: 获取商品缓存（待实现商品模块后启用）
     *
     * @param productId 商品ID
     * @return 商品对象
     */
    public Object getProductFromCache(Long productId) {
        log.warn("商品缓存功能待实现: productId={}", productId);
        return null;
    }

    /**
     * TODO: 将商品放入缓存（待实现商品模块后启用）
     *
     * @param product 商品对象
     */
    public void putProductToCache(Object product) {
        log.warn("商品缓存功能待实现");
    }

    /**
     * TODO: 删除商品缓存（待实现商品模块后启用）
     *
     * @param productId 商品ID
     */
    public void deleteProductFromCache(Long productId) {
        log.warn("商品缓存功能待实现: productId={}", productId);
    }
}