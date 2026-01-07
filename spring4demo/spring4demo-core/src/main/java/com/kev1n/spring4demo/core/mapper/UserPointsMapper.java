package com.kev1n.spring4demo.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kev1n.spring4demo.core.entity.UserPoints;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户积分Mapper
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Mapper
public interface UserPointsMapper extends BaseMapper<UserPoints> {
}