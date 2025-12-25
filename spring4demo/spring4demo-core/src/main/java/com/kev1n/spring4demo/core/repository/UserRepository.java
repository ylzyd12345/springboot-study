package com.kev1n.spring4demo.core.repository;

import com.kev1n.spring4demo.core.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问层
 * 使用Spring Data JPA
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 根据状态查找用户
     */
    List<User> findByStatus(Integer status);

    /**
     * 根据用户名模糊查询用户（分页）
     */
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    /**
     * 根据部门ID查找用户
     */
    List<User> findByDeptId(String deptId);

    /**
     * 根据状态和部门ID查找用户
     */
    List<User> findByStatusAndDeptId(Integer status, String deptId);

    /**
     * 统计指定状态的用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
    long countByStatus(@Param("status") Integer status);

    /**
     * 查找最近创建的用户
     */
    @Query("SELECT u FROM User u WHERE u.status = 1 ORDER BY u.createTime DESC")
    List<User> findRecentActiveUsers();

    }