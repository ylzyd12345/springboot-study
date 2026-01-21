package com.junmo.platform.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junmo.platform.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问使用MyBatis-Plus
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查找用户
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     */
    @Select("SELECT * FROM sys_user WHERE email = #{email} AND deleted = 0")
    Optional<User> findByEmail(String email);

    /**
     * 检查用户名是否存在
     */
    @Select("SELECT COUNT(1) FROM sys_user WHERE username = #{username} AND deleted = 0")
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    @Select("SELECT COUNT(1) FROM sys_user WHERE email = #{email} AND deleted = 0")
    boolean existsByEmail(String email);

    /**
     * 根据状态查找用户
     */
    @Select("SELECT * FROM sys_user WHERE status = #{status} AND deleted = 0")
    List<User> findByStatus(Integer status);

    /**
     * 根据用户名模糊查询用户（分页）
     */
    @Select("SELECT * FROM sys_user WHERE username LIKE CONCAT('%', #{username}, '%') AND deleted = 0")
    Page<User> findByUsernameContainingIgnoreCase(@Param("username") String username, Page<User> pageable);

    /**
     * 统计指定状态的用户数量
     */
    @Select("SELECT COUNT(1) FROM sys_user WHERE status = #{status} AND deleted = 0")
    long countByStatus(@Param("status") Integer status);

    /**
     * 查找最近创建的用户
     */
    @Select("SELECT * FROM sys_user WHERE status = 1 AND deleted = 0 ORDER BY created_at DESC")
    List<User> findRecentActiveUsers();
}