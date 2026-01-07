package com.kev1n.spring4demo.core.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kev1n.spring4demo.core.entity.User;
import com.kev1n.spring4demo.core.mapper.UserMapper;
import com.kev1n.spring4demo.core.repository.UserReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

/**
 * 用户响应式数据访问实现类
 * 基于MyBatis-Plus实现响应式数据访问
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserReactiveRepositoryImpl implements UserReactiveRepository {

    private final UserMapper userMapper;

    @Override
    public Flux<User> findAll() {
        return Flux.defer(() -> Flux.fromIterable(userMapper.selectList(null)))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnComplete(() -> log.debug("查询所有用户完成"));
    }

    @Override
    public Mono<User> findById(Long id) {
        return Mono.fromCallable(() -> userMapper.selectById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(user -> log.debug("根据ID查询用户: {}", id));
    }

    @Override
    public Mono<User> findByUsername(String username) {
        return Mono.fromCallable(() -> userMapper.findByUsername(username).orElse(null))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(user -> log.debug("根据用户名查询用户: {}", username));
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return Mono.fromCallable(() -> userMapper.findByEmail(email).orElse(null))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(user -> log.debug("根据邮箱查询用户: {}", email));
    }

    @Override
    public Flux<User> findByStatus(Integer status) {
        return Flux.defer(() -> Flux.fromIterable(userMapper.findByStatus(status)))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnComplete(() -> log.debug("根据状态查询用户完成: {}", status));
    }

    @Override
    public Mono<User> save(User user) {
        return Mono.fromCallable(() -> {
            userMapper.insert(user);
            return user;
        }).subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(savedUser -> log.debug("保存用户成功: {}", savedUser.getId()));
    }

    @Override
    public Mono<User> update(User user) {
        return Mono.fromCallable(() -> {
            userMapper.updateById(user);
            return user;
        }).subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(updatedUser -> log.debug("更新用户成功: {}", updatedUser.getId()));
    }

    @Override
    public Mono<Boolean> deleteById(Long id) {
        return Mono.fromCallable(() -> userMapper.deleteById(id) > 0)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(deleted -> log.debug("删除用户: {}, 结果: {}", id, deleted));
    }

    @Override
    public Mono<Boolean> existsByUsername(String username) {
        return Mono.fromCallable(() -> userMapper.existsByUsername(username))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(exists -> log.debug("检查用户名是否存在: {}, 结果: {}", username, exists));
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return Mono.fromCallable(() -> userMapper.existsByEmail(email))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(exists -> log.debug("检查邮箱是否存在: {}, 结果: {}", email, exists));
    }

    @Override
    public Mono<Long> countByStatus(Integer status) {
        return Mono.fromCallable(() -> userMapper.countByStatus(status))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(count -> log.debug("统计指定状态的用户数量: {}, 结果: {}", status, count));
    }

    @Override
    public Flux<User> streamUsers() {
        return Flux.interval(java.time.Duration.ofSeconds(1))
                .flatMap(tick -> findAll())
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSubscribe(subscription -> log.debug("开始流式查询用户数据"));
    }

    @Override
    public Flux<User> findAllById(Iterable<Long> ids) {
        return Flux.defer(() -> {
            List<Long> idList = new java.util.ArrayList<>();
            ids.forEach(idList::add);
            return Flux.fromIterable(userMapper.selectBatchIds(idList));
        }).subscribeOn(Schedulers.boundedElastic())
                .doOnComplete(() -> log.debug("批量查询用户完成"));
    }
}