package com.kev1n.spring4demo.core.repository.elasticsearch;

import com.kev1n.spring4demo.core.document.UserDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * 用户文档Repository
 *
 * @author spring4demo
 * @version 1.0.0
 */
public interface UserDocumentRepository extends ElasticsearchRepository<UserDocument, String> {

    /**
     * 全文搜索（搜索用户名或真实姓名）
     *
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 用户文档分页结果
     */
    Page<UserDocument> findByUsernameOrRealName(String keyword, String keyword2, Pageable pageable);

    /**
     * 根据用户名搜索
     *
     * @param username 用户名
     * @param pageable 分页参数
     * @return 用户文档分页结果
     */
    Page<UserDocument> findByUsername(String username, Pageable pageable);

    /**
     * 根据真实姓名搜索
     *
     * @param realName 真实姓名
     * @param pageable 分页参数
     * @return 用户文档分页结果
     */
    Page<UserDocument> findByRealName(String realName, Pageable pageable);

    /**
     * 根据邮箱搜索
     *
     * @param email 邮箱
     * @param pageable 分页参数
     * @return 用户文档分页结果
     */
    Page<UserDocument> findByEmail(String email, Pageable pageable);

    /**
     * 根据手机号搜索
     *
     * @param phone 手机号
     * @param pageable 分页参数
     * @return 用户文档分页结果
     */
    Page<UserDocument> findByPhone(String phone, Pageable pageable);

    /**
     * 根据状态查询用户
     *
     * @param status 状态
     * @param pageable 分页参数
     * @return 用户文档分页结果
     */
    Page<UserDocument> findByStatus(Integer status, Pageable pageable);

    /**
     * 根据部门ID查询用户
     *
     * @param deptId 部门ID
     * @param pageable 分页参数
     * @return 用户文档分页结果
     */
    Page<UserDocument> findByDeptId(String deptId, Pageable pageable);

    /**
     * 根据状态和部门ID查询用户
     *
     * @param status 状态
     * @param deptId 部门ID
     * @param pageable 分页参数
     * @return 用户文档分页结果
     */
    Page<UserDocument> findByStatusAndDeptId(Integer status, String deptId, Pageable pageable);

    /**
     * 根据用户名模糊搜索
     *
     * @param username 用户名
     * @param pageable 分页参数
     * @return 用户文档分页结果
     */
    @Query("{\"bool\": {\"must\": [{\"match\": {\"username\": \"?0\"}}]}}")
    Page<UserDocument> searchByUsername(String username, Pageable pageable);

    /**
     * 根据真实姓名模糊搜索
     *
     * @param realName 真实姓名
     * @param pageable 分页参数
     * @return 用户文档分页结果
     */
    @Query("{\"bool\": {\"must\": [{\"match\": {\"realName\": \"?0\"}}]}}")
    Page<UserDocument> searchByRealName(String realName, Pageable pageable);

    /**
     * 多字段搜索（用户名、真实姓名、邮箱）
     *
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 用户文档分页结果
     */
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"username^2\", \"realName^2\", \"email\"]}}")
    Page<UserDocument> searchByMultiField(String keyword, Pageable pageable);

    /**
     * 根据状态查询用户列表
     *
     * @param status 状态
     * @return 用户文档列表
     */
    List<UserDocument> findByStatus(Integer status);

    /**
     * 查询所有未删除的用户
     *
     * @param pageable 分页参数
     * @return 用户文档分页结果
     */
    Page<UserDocument> findByDeleted(Integer deleted, Pageable pageable);

    /**
     * 根据用户ID查询
     *
     * @param userId 用户ID
     * @return 用户文档
     */
    UserDocument findByUserId(Long userId);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
}