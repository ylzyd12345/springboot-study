package com.kev1n.spring4demo.core.repository.neo4j;

import com.kev1n.spring4demo.core.entity.neo4j.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户图数据库仓库
 *
 * <p>提供用户节图的 CRUD 操作和图查询功能。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Repository
public interface UserRepository extends Neo4jRepository<UserNode, Long> {

    /**
     * 根据用户名查找用户节点
     *
     * @param username 用户名
     * @return 用户节点
     */
    Optional<UserNode> findByUsername(String username);

    /**
     * 根据邮箱查找用户节点
     *
     * @param email 邮箱
     * @return 用户节点
     */
    Optional<UserNode> findByEmail(String email);

    /**
     * 根据手机号查找用户节点
     *
     * @param phone 手机号
     * @return 用户节点
     */
    Optional<UserNode> findByPhone(String phone);

    /**
     * 根据状态查找用户节点列表
     *
     * @param status 状态
     * @return 用户节点列表
     */
    List<UserNode> findByStatus(Integer status);

    /**
     * 根据状态和创建时间范围查找用户节点
     *
     * @param status 状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 用户节点列表
     */
    @Query("MATCH (u:User) WHERE u.status = $status AND u.createdAt >= $startTime AND u.createdAt <= $endTime RETURN u")
    List<UserNode> findByStatusAndCreateTimeBetween(Integer status, String startTime, String endTime);

    /**
     * 查找最近登录的用户节点
     *
     * @param limit 限制数量
     * @return 用户节点列表
     */
    @Query("MATCH (u:User) WHERE u.lastLoginTime IS NOT NULL RETURN u ORDER BY u.lastLoginTime DESC LIMIT $limit")
    List<UserNode> findRecentlyLoggedInUsers(Integer limit);

    /**
     * 查找登录次数最多的用户节点
     *
     * @param limit 限制数量
     * @return 用户节点列表
     */
    @Query("MATCH (u:User) WHERE u.loginCount > 0 RETURN u ORDER BY u.loginCount DESC LIMIT $limit")
    List<UserNode> findMostActiveUsers(Integer limit);

    // ==================== TODO: 待添加的关系查询 ====================
    // TODO: 当实现用户关系功能时，添加以下查询方法：
    // - findFollowers(userId): 查找用户的粉丝
    // - findFollowing(userId): 查找用户关注的人
    // - findFriends(userId): 查找用户的好友
    // - findColleagues(userId): 查找用户的同事
    // - findShortestPath(userId1, userId2): 查找两个用户之间的最短路径
    // - findMutualFriends(userId1, userId2): 查找两个用户的共同好友
    // - findRecommendedFriends(userId): 推荐好友（基于共同好友数）
}