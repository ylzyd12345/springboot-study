package com.kev1n.spring4demo.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kev1n.spring4demo.core.entity.UserAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户账户Mapper
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Mapper
public interface UserAccountMapper extends BaseMapper<UserAccount> {
}