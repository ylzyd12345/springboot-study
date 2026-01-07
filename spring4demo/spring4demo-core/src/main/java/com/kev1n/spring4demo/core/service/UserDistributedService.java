package com.kev1n.spring4demo.core.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kev1n.spring4demo.api.dto.UserCreateDTO;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.entity.UserAccount;
import com.kev1n.spring4demo.core.entity.UserPoints;
import com.kev1n.spring4demo.core.mapper.UserAccountMapper;
import com.kev1n.spring4demo.core.mapper.UserPointsMapper;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 用户分布式事务服务
 *
 * 提供涉及多个数据表的分布式事务方法，使用Seata AT模式保证数据一致性
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDistributedService {

    private final UserService userService;
    private final UserAccountMapper userAccountMapper;
    private final UserPointsMapper userPointsMapper;

    /**
     * 用户注册分布式事务
     *
     * 涉及三个表的原子操作：
     * 1. 创建用户信息（sys_user表）
     * 2. 创建用户账户（sys_user_account表）
     * 3. 创建用户积分（sys_user_points表）
     *
     * 如果任意一步失败，Seata会自动回滚所有操作
     *
     * @param dto 用户创建DTO
     * @return 创建的用户对象
     */
    @GlobalTransactional(name = "register-user", rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    public User registerUser(UserCreateDTO dto) {
        log.info("开始用户注册分布式事务: username={}", dto.getUsername());

        try {
            // 1. 创建用户信息
            User user = new User();
            user.setUsername(dto.getUsername());
            user.setPassword(dto.getPassword());
            user.setEmail(dto.getEmail());
            user.setPhone(dto.getPhone());
            user.setRealName(dto.getRealName());
            user.setStatus(1); // 默认状态：正常

            userService.save(user);
            log.info("用户信息创建成功: userId={}", user.getId());

            // 2. 创建用户账户（初始余额为0）
            UserAccount account = new UserAccount();
            account.setUserId(user.getId());
            account.setBalance(BigDecimal.ZERO);
            account.setFrozenAmount(BigDecimal.ZERO);
            account.setAvailableBalance(BigDecimal.ZERO);
            account.setStatus(0); // 默认状态：正常

            userAccountMapper.insert(account);
            log.info("用户账户创建成功: accountId={}", account.getId());

            // 3. 创建用户积分（初始积分为0）
            UserPoints points = new UserPoints();
            points.setUserId(user.getId());
            points.setPoints(0);
            points.setTotalEarned(0);
            points.setTotalConsumed(0);
            points.setLevel(1); // 默认等级：1
            points.setStatus(0); // 默认状态：正常

            userPointsMapper.insert(points);
            log.info("用户积分创建成功: pointsId={}", points.getId());

            log.info("用户注册分布式事务提交成功: userId={}", user.getId());
            return user;

        } catch (Exception e) {
            log.error("用户注册分布式事务失败，Seata将自动回滚: username={}",
                    dto.getUsername(), e);
            throw new RuntimeException("用户注册失败: " + e.getMessage(), e);
        }
    }

    /**
     * 用户充值分布式事务
     *
     * 涉及两个表的原子操作：
     * 1. 增加用户账户余额（sys_user_account表）
     * 2. 增加用户积分（sys_user_points表）
     *
     * 如果任意一步失败，Seata会自动回滚所有操作
     *
     * @param userId 用户ID
     * @param amount 充值金额
     * @param points 获得积分
     * @return 是否充值成功
     */
    @GlobalTransactional(name = "user-recharge", rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    public boolean recharge(Long userId, BigDecimal amount, Integer points) {
        log.info("开始用户充值分布式事务: userId={}, amount={}, points={}",
                userId, amount, points);

        try {
            // 1. 查询用户账户
            UserAccount account = userAccountMapper.selectOne(
                    new LambdaQueryWrapper<UserAccount>()
                            .eq(UserAccount::getUserId, userId)
            );

            if (account == null) {
                throw new RuntimeException("用户账户不存在: userId=" + userId);
            }

            // 2. 增加账户余额
            BigDecimal newBalance = account.getBalance().add(amount);
            BigDecimal newAvailableBalance = account.getAvailableBalance().add(amount);

            account.setBalance(newBalance);
            account.setAvailableBalance(newAvailableBalance);

            int accountRows = userAccountMapper.updateById(account);
            if (accountRows == 0) {
                throw new RuntimeException("更新用户账户余额失败: userId=" + userId);
            }

            log.info("用户账户余额更新成功: userId={}, newBalance={}", userId, newBalance);

            // 3. 查询用户积分
            UserPoints userPoints = userPointsMapper.selectOne(
                    new LambdaQueryWrapper<UserPoints>()
                            .eq(UserPoints::getUserId, userId)
            );

            if (userPoints == null) {
                throw new RuntimeException("用户积分不存在: userId=" + userId);
            }

            // 4. 增加用户积分
            Integer newPoints = userPoints.getPoints() + points;
            Integer newTotalEarned = userPoints.getTotalEarned() + points;

            userPoints.setPoints(newPoints);
            userPoints.setTotalEarned(newTotalEarned);

            // 更新积分等级（每1000积分升一级）
            Integer newLevel = newPoints / 1000 + 1;
            userPoints.setLevel(newLevel);

            int pointsRows = userPointsMapper.updateById(userPoints);
            if (pointsRows == 0) {
                throw new RuntimeException("更新用户积分失败: userId=" + userId);
            }

            log.info("用户积分更新成功: userId={}, newPoints={}, newLevel={}",
                    userId, newPoints, newLevel);

            log.info("用户充值分布式事务提交成功: userId={}", userId);
            return true;

        } catch (Exception e) {
            log.error("用户充值分布式事务失败，Seata将自动回滚: userId={}", userId, e);
            throw new RuntimeException("用户充值失败: " + e.getMessage(), e);
        }
    }

    /**
     * 用户转账分布式事务
     *
     * 涉及两个用户账户的原子操作：
     * 1. 扣减用户A的账户余额（sys_user_account表）
     * 2. 增加用户B的账户余额（sys_user_account表）
     *
     * 如果任意一步失败，Seata会自动回滚所有操作
     *
     * @param fromUserId 转出用户ID
     * @param toUserId 转入用户ID
     * @param amount 转账金额
     * @return 是否转账成功
     */
    @GlobalTransactional(name = "user-transfer", rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    public boolean transfer(Long fromUserId, Long toUserId, BigDecimal amount) {
        log.info("开始用户转账分布式事务: fromUserId={}, toUserId={}, amount={}",
                fromUserId, toUserId, amount);

        try {
            // 1. 查询转出用户账户
            UserAccount fromAccount = userAccountMapper.selectOne(
                    new LambdaQueryWrapper<UserAccount>()
                            .eq(UserAccount::getUserId, fromUserId)
            );

            if (fromAccount == null) {
                throw new RuntimeException("转出用户账户不存在: fromUserId=" + fromUserId);
            }

            // 2. 检查转出用户余额是否充足
            if (fromAccount.getAvailableBalance().compareTo(amount) < 0) {
                throw new RuntimeException("转出用户余额不足: fromUserId=" + fromUserId
                        + ", availableBalance=" + fromAccount.getAvailableBalance()
                        + ", amount=" + amount);
            }

            // 3. 扣减转出用户余额
            BigDecimal fromNewBalance = fromAccount.getBalance().subtract(amount);
            BigDecimal fromNewAvailableBalance = fromAccount.getAvailableBalance().subtract(amount);

            fromAccount.setBalance(fromNewBalance);
            fromAccount.setAvailableBalance(fromNewAvailableBalance);

            int fromRows = userAccountMapper.updateById(fromAccount);
            if (fromRows == 0) {
                throw new RuntimeException("更新转出用户账户余额失败: fromUserId=" + fromUserId);
            }

            log.info("转出用户账户余额扣减成功: fromUserId={}, newBalance={}",
                    fromUserId, fromNewBalance);

            // 4. 查询转入用户账户
            UserAccount toAccount = userAccountMapper.selectOne(
                    new LambdaQueryWrapper<UserAccount>()
                            .eq(UserAccount::getUserId, toUserId)
            );

            if (toAccount == null) {
                throw new RuntimeException("转入用户账户不存在: toUserId=" + toUserId);
            }

            // 5. 增加转入用户余额
            BigDecimal toNewBalance = toAccount.getBalance().add(amount);
            BigDecimal toNewAvailableBalance = toAccount.getAvailableBalance().add(amount);

            toAccount.setBalance(toNewBalance);
            toAccount.setAvailableBalance(toNewAvailableBalance);

            int toRows = userAccountMapper.updateById(toAccount);
            if (toRows == 0) {
                throw new RuntimeException("更新转入用户账户余额失败: toUserId=" + toUserId);
            }

            log.info("转入用户账户余额增加成功: toUserId={}, newBalance={}",
                    toUserId, toNewBalance);

            log.info("用户转账分布式事务提交成功: fromUserId={}, toUserId={}",
                    fromUserId, toUserId);
            return true;

        } catch (Exception e) {
            log.error("用户转账分布式事务失败，Seata将自动回滚: fromUserId={}, toUserId={}",
                    fromUserId, toUserId, e);
            throw new RuntimeException("用户转账失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建用户订单分布式事务
     *
     * TODO: 待实现订单服务后启用
     *
     * 涉及多个表的原子操作：
     * 1. 创建订单（sys_order表）- 待实现
     * 2. 扣减用户余额（sys_user_account表）
     * 3. 扣减用户积分（sys_user_points表）
     *
     * @param userId 用户ID
     * @param amount 订单金额
     * @param points 消耗积分
     */
    @GlobalTransactional(name = "create-user-order", rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    public void createUserOrder(Long userId, BigDecimal amount, Integer points) {
        log.info("开始创建用户订单分布式事务: userId={}, amount={}, points={}",
                userId, amount, points);

        try {
            // TODO: 实现订单创建逻辑
            // Order order = orderService.createOrder(userId, amount);
            log.info("订单服务待实现");

            // TODO: 扣减用户余额和积分
            log.info("订单创建功能待实现，订单服务开发后需要补充完整逻辑");

        } catch (Exception e) {
            log.error("创建用户订单分布式事务失败，Seata将自动回滚: userId={}", userId, e);
            throw new RuntimeException("创建用户订单失败: " + e.getMessage(), e);
        }
    }
}