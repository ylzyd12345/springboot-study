package com.junmo.platform.core.service;

import com.junmo.platform.model.entity.User;
import com.junmo.platform.model.entity.neo4j.UserNode;
import com.junmo.platform.model.repository.neo4j.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 用户图数据库服务
 *
 * <p>提供用户节点的 CRUD 操作和图查询功能，支持从关系型数据库同步数据到图数据库。</p>
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserGraphService {

    private final UserRepository userRepository;
    private final UserService userService;

    /**
     * 同步用户到图数据库
     *
     * <p>从关系型数据库同步用户数据到 Neo4j 图数据库。</p>
     *
     * @param user 用户实体
     * @return 用户节点
     */
    @Transactional
    public UserNode syncUserToGraph(User user) {
        log.info("同步用户到图数据库: userId={}, username={}", user.getId(), user.getUsername());

        UserNode userNode = UserNode.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .realName(user.getRealName())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
//                .lastLoginTime(user.getLastLoginTime())
//                .loginCount(user.getLoginCount())
                .build();

        return userRepository.save(userNode);
    }

    /**
     * 批量同步用户到图数据库
     *
     * @param userIds 用户ID列表
     * @return 同步的用户节点数量
     */
    @Transactional
    public int batchSyncUsersToGraph(List<Long> userIds) {
        log.info("批量同步用户到图数据库: count={}", userIds.size());

        int count = 0;
        for (Long userId : userIds) {
            User userOptional = userService.getById(userId);
            syncUserToGraph(userOptional);
            count++;
        }

        log.info("批量同步完成: successCount={}", count);
        return count;
    }

    /**
     * 根据用户ID查找用户节点
     *
     * @param userId 用户ID
     * @return 用户节点
     */
    public Optional<UserNode> getUserNodeById(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * 根据用户名查找用户节点
     *
     * @param username 用户名
     * @return 用户节点
     */
    public Optional<UserNode> getUserNodeByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 根据邮箱查找用户节点
     *
     * @param email 邮箱
     * @return 用户节点
     */
    public Optional<UserNode> getUserNodeByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 根据手机号查找用户节点
     *
     * @param phone 手机号
     * @return 用户节点
     */
    public Optional<UserNode> getUserNodeByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    /**
     * 根据状态查找用户节点列表
     *
     * @param status 状态
     * @return 用户节点列表
     */
    public List<UserNode> getUserNodesByStatus(Integer status) {
        return userRepository.findByStatus(status);
    }

    /**
     * 查找最近登录的用户节点
     *
     * @param limit 限制数量
     * @return 用户节点列表
     */
    public List<UserNode> getRecentlyLoggedInUsers(Integer limit) {
        return userRepository.findRecentlyLoggedInUsers(limit);
    }

    /**
     * 查找登录次数最多的用户节点
     *
     * @param limit 限制数量
     * @return 用户节点列表
     */
    public List<UserNode> getMostActiveUsers(Integer limit) {
        return userRepository.findMostActiveUsers(limit);
    }

    /**
     * 更新用户节点
     *
     * @param user 用户实体
     * @return 更新后的用户节点
     */
    @Transactional
    public UserNode updateUserNode(User user) {
        log.info("更新用户节点: userId={}, username={}", user.getId(), user.getUsername());

        Optional<UserNode> userNodeOptional = userRepository.findById(user.getId());
        if (userNodeOptional.isPresent()) {
            UserNode userNode = userNodeOptional.get();
            userNode.setUsername(user.getUsername());
            userNode.setEmail(user.getEmail());
            userNode.setPhone(user.getPhone());
            userNode.setRealName(user.getRealName());
            userNode.setStatus(user.getStatus());
            userNode.setUpdatedAt(user.getUpdatedAt());
//            userNode.setLastLoginTime(user.getLastLoginTime());
//            userNode.setLoginCount(user.getLoginCount());

            return userRepository.save(userNode);
        } else {
            return syncUserToGraph(user);
        }
    }

    /**
     * 删除用户节点
     *
     * @param userId 用户ID
     */
    @Transactional
    public void deleteUserNode(Long userId) {
        log.info("删除用户节点: userId={}", userId);
        userRepository.deleteById(userId);
    }

    /**
     * 统计用户节点数量
     *
     * @return 用户节点数量
     */
    public long countUserNodes() {
        return userRepository.count();
    }

    // ==================== TODO: 待添加的关系操作方法 ====================
    // TODO: 当实现用户关系功能时，添加以下方法：
    // - addFollowRelation(followerId, followingId): 添加关注关系
    // - removeFollowRelation(followerId, followingId): 删除关注关系
    // - addFriendRelation(userId1, userId2): 添加好友关系
    // - removeFriendRelation(userId1, userId2): 删除好友关系
    // - findFollowers(userId): 查找用户的粉丝
    // - findFollowing(userId): 查找用户关注的人
    // - findFriends(userId): 查找用户的好友
    // - findShortestPath(userId1, userId2): 查找两个用户之间的最短路径
    // - findMutualFriends(userId1, userId2): 查找两个用户的共同好友
    // - findRecommendedFriends(userId): 推荐好友（基于共同好友数）
    // - getUserGraphStatistics(userId): 获取用户图统计数据
}