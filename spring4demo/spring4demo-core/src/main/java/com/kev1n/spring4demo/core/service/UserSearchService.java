package com.kev1n.spring4demo.core.service;

import com.kev1n.spring4demo.core.document.UserDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 用户搜索服务接口
 *
 * @author spring4demo
 * @version 1.0.0
 */
public interface UserSearchService {

    /**
     * 索引单个用户
     *
     * @param userId 用户ID
     */
    void indexUser(Long userId);

    /**
     * 批量索引用户
     *
     * @param userIds 用户ID列表
     */
    void indexUsers(List<Long> userIds);

    /**
     * 重建索引（清空后重新索引所有用户）
     */
    void rebuildIndex();

    /**
     * 删除用户索引
     *
     * @param userId 用户ID
     */
    void deleteUserIndex(Long userId);

    /**
     * 批量删除用户索引
     *
     * @param userIds 用户ID列表
     */
    void deleteUserIndexes(List<Long> userIds);

    /**
     * 全文搜索用户
     *
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 用户文档分页结果
     */
    Page<UserDocument> searchUsers(String keyword, Pageable pageable);

    /**
     * 高级搜索用户
     *
     * @param keyword 关键词
     * @param status 状态
     * @param deptId 部门ID
     * @param pageable 分页参数
     * @return 用户文档分页结果
     */
    Page<UserDocument> advancedSearch(String keyword, Integer status, String deptId, Pageable pageable);

    /**
     * 根据用户名搜索
     *
     * @param username 用户名
     * @param pageable 分页参数
     * @return 用户文档分页结果
     */
    Page<UserDocument> searchByUsername(String username, Pageable pageable);

    /**
     * 根据真实姓名搜索
     *
     * @param realName 真实姓名
     * @param pageable 分页参数
     * @return 用户文档分页结果
     */
    Page<UserDocument> searchByRealName(String realName, Pageable pageable);

    /**
     * 根据邮箱搜索
     *
     * @param email 邮箱
     * @param pageable 分页参数
     * @return 用户文档分页结果
     */
    Page<UserDocument> searchByEmail(String email, Pageable pageable);

    /**
     * 根据状态搜索
     *
     * @param status 状态
     * @param pageable 分页参数
     * @return 用户文档分页结果
     */
    Page<UserDocument> searchByStatus(Integer status, Pageable pageable);

    /**
     * 根据部门搜索
     *
     * @param deptId 部门ID
     * @param pageable 分页参数
     * @return 用户文档分页结果
     */
    Page<UserDocument> searchByDeptId(String deptId, Pageable pageable);

    /**
     * 根据用户ID查询
     *
     * @param userId 用户ID
     * @return 用户文档
     */
    UserDocument getUserDocumentById(Long userId);

    /**
     * 同步用户到ES（更新或创建）
     *
     * @param userId 用户ID
     */
    void syncUserToEs(Long userId);

    /**
     * 批量同步用户到ES
     *
     * @param userIds 用户ID列表
     */
    void syncUsersToEs(List<Long> userIds);
}