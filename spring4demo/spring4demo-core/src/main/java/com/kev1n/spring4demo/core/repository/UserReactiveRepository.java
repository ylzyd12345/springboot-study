package com.kev1n.spring4demo.core.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kev1n.spring4demo.core.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Repository
 * 使用MyBatis-Plus实现数据访问
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Mapper
public interface UserReactiveRepository extends BaseMapper<User> {

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    User findByUsername(@Param("username") String username);

    /**
     * 根据邮箱查找用户
     *
     * @param email 邮箱
     * @return 用户
     */
    @Select("SELECT * FROM sys_user WHERE email = #{email} AND deleted = 0")
    User findByEmail(@Param("email") String email);

    /**
     * 根据状态查找用户列表
     *
     * @param status 状态
     * @return 用户列表
     */
    @Select("SELECT * FROM sys_user WHERE status = #{status} AND deleted = 0")
    List<User> findByStatus(@Param("status") Integer status);

    /**
     * 查找所有活跃用户
     *
     * @return 用户列表
     */
    @Select("SELECT * FROM sys_user WHERE status = 1 AND deleted = 0")
    List<User> findActiveUsers();

    /**
     * 根据部门ID查找用户
     *
     * @param deptId 部门ID
     * @return 用户列表
     */
    @Select("SELECT * FROM sys_user WHERE dept_id = #{deptId} AND deleted = 0")
    List<User> findByDeptId(@Param("deptId") String deptId);
}