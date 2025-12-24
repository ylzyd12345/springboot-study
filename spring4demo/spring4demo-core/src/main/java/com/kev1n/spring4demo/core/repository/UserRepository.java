package com.kev1n.spring4demo.core.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kev1n.spring4demo.core.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问层
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Mapper
public interface UserRepository extends BaseMapper<User> {
}